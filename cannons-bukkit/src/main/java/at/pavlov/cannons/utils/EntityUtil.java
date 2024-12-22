package at.pavlov.cannons.utils;

import at.pavlov.bukkit.container.BukkitTarget;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.cannons.TargetManager;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.internal.enums.BreakCause;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityUtil {

    /**
     * searches for destroyed cannons in the explosion event and removes cannons parts which can't be destroyed in an explosion.
     * @param blocklist list of blocks involved in the event
     */
    public static void handleExplosionEvent(List<Block> blocklist){
        HashSet<UUID> remove = new HashSet<>();
        CannonManager cannonManager = CannonManager.getInstance();

        // first search if a barrel block was destroyed.
        for (Block block : blocklist) {
            Cannon cannon = cannonManager.getCannon(block.getLocation(), null);

            // if it is a cannon block
            if (cannon == null) {
                continue;
            }

            if (cannon.isDestructibleBlock(block.getLocation())) {
                //this cannon is destroyed
                remove.add(cannon.getUID());
            }
        }

        //iterate again and remove all block of intact cannons
        for (int i = 0; i < blocklist.size(); i++) {
            Block block = blocklist.get(i);
            Cannon cannon = cannonManager.getCannon(block.getLocation(), null);

            // if it is a cannon block and the cannon is not destroyed (see above)
            if (cannon == null || remove.contains(cannon.getUID())) {
                continue;
            }

            if (cannon.isCannonBlock(block)) {
                blocklist.remove(i--);
            }
        }

        //now remove all invalid cannons
        for (UUID id : remove)
            cannonManager.removeCannon(id, false, true, BreakCause.EXPLOSION);
    }

    /**
     * returns all entity in a given radius
     * @param l center location
     * @param minRadius minimum radius for search
     * @param maxRadius radius for search
     * @return hashmap of Entities in area
     */
    public static HashMap<UUID, Entity> getNearbyEntities(Location l, int minRadius, int maxRadius){
        int chunkRadius = maxRadius < 16 ? 1 : (maxRadius - (maxRadius % 16))/16;
        HashMap<UUID, Entity> radiusEntities = new HashMap<>();
        for (int chX = -chunkRadius; chX <= chunkRadius; chX ++){
            for (int chZ = -chunkRadius; chZ <= chunkRadius; chZ++){
                int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
                for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                    double dist = e.getLocation().distance(l);
                    if (minRadius <= dist && dist <= maxRadius && e.getLocation().getBlock() != l.getBlock())
                        radiusEntities.put(e.getUniqueId(), e);
                }
            }
        }
        return radiusEntities;
    }

    /**
     * This method searches in the nearby chunks in a square and
     * returns all targets (entity and cannons) in a given radius
     * @param l center location
     * @param minRadius minimum radius for search
     * @param maxRadius radius for search
     * @return array of Entities in area
     */
    public static HashMap<UUID, BukkitTarget> getNearbyTargets(Location l, int minRadius, int maxRadius){
        int chunkTargets = maxRadius < 16 ? 1 : (maxRadius - (maxRadius % 16))/16;
        HashMap<UUID, BukkitTarget> radiusTargets = new HashMap<>();

        for (int chX = -chunkTargets; chX <= chunkTargets; chX++){
            for (int chZ = -chunkTargets; chZ <= chunkTargets; chZ++){

                int x=(int) l.getX(), y=(int) l.getY(), z=(int) l.getZ();

                for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
                    if (!e.getWorld().equals(l.getWorld())) {
                        continue;
                    }


                    if (!(e instanceof LivingEntity) || e.isDead() || e.getLocation().getBlock() == l.getBlock()) {
                        continue;
                    }

                    double dist = e.getLocation().distanceSquared(l);
                    if (maxRadius * maxRadius < dist || dist < minRadius * minRadius) {
                        continue;
                    }

                    if (e instanceof Player p){
                        if (p.getGameMode() == GameMode.CREATIVE || p.hasPermission("cannons.admin.notarget"))
                            continue;
                    }

                    radiusTargets.put(e.getUniqueId(), new BukkitTarget(e));
                }
            }
        }
        for (Cannon cannon : CannonManager.getInstance().getCannonsInSphere(l, maxRadius))
            if (cannon.getRandomBarrelBlock().distanceSquared(l) > minRadius * minRadius)
                radiusTargets.put(cannon.getUID(), new BukkitTarget(cannon));

        // additional targets from different plugins e.g. ships
        for (BukkitTarget target : TargetManager.getTargetsInSphere(l, maxRadius))
            if (target.centerLocation().getVector().distanceSquared(VectorUtils.fromLoc(l)) > minRadius * minRadius)
                radiusTargets.put(target.uniqueId(), target);
        return radiusTargets;
    }
}
