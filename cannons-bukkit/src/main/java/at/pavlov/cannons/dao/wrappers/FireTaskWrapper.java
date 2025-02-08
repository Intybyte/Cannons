package at.pavlov.cannons.dao.wrappers;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.Enum.FakeBlockType;
import at.pavlov.cannons.Enum.ProjectileCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileManager;
import at.pavlov.cannons.projectile.ProjectileProperties;
import at.pavlov.cannons.scheduler.FakeBlockHandler;
import at.pavlov.cannons.utils.SoundUtils;
import com.cryptomorin.xseries.XPotion;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record FireTaskWrapper(
        Cannon cannon,
        UUID shooter,
        boolean removeCharge,
        ProjectileCause projectileCause
) {
    private static final Cannons plugin = Cannons.getPlugin();

    /**
     * fires a cannon and removes the charge from the shooter
     */
    public void fireTask() {
        CannonDesign design = cannon.getCannonDesign();
        Projectile projectile = cannon.getLoadedProjectile();

        //the shooter might be null if not online
        Player onlinePlayer = Bukkit.getPlayer(shooter);

        // no projectile no cannon firing
        if (projectile == null) return;
        // no gunpowder no cannon firing
        if (cannon.getLoadedGunpowder() <= 0) return;

        //increased fired cannonballs
        cannon.incrementFiredCannonballs();

        //get firing location
        Location firingLoc = design.getMuzzle(cannon);
        World world = cannon.getWorldBukkit();

        //Muzzle flash + effects
        if (projectile.getProjectileEntity().equals(EntityType.ARROW))
            world.playEffect(firingLoc, Effect.BOW_FIRE, 10);
        muzzleFire();
        world.playEffect(firingLoc, Effect.SMOKE, cannon.getCannonDirection());

        //increase heat of the cannon
        if (design.isHeatManagementEnabled())
            cannon.setTemperature(cannon.getTemperature() + design.getHeatIncreasePerGunpowder() / projectile.getAutomaticFiringMagazineSize() * cannon.getLoadedGunpowder());
        //automatic cool down
        if (design.isAutomaticCooling())
            cannon.automaticCooling();

        //for each bullet, but at least once
        for (int i = 0; i < Math.max(projectile.getNumberOfBullets(), 1); i++) {
            org.bukkit.projectiles.ProjectileSource source = null;
            Location playerLoc = null;
            if (onlinePlayer != null) {
                source = onlinePlayer;
                playerLoc = onlinePlayer.getLocation();
            }

            Vector vect = cannon.getFiringVector(true, true);

            org.bukkit.entity.Projectile projectileEntity = ProjectileManager.getInstance().spawnProjectile(projectile, shooter, source, playerLoc, firingLoc, vect, cannon.getUID(), projectileCause);

            if (i == 0 && projectile.hasProperty(ProjectileProperties.SHOOTER_AS_PASSENGER) && onlinePlayer != null)
                projectileEntity.setPassenger(onlinePlayer);

            //confuse all entity which wear no helmets due to the blast of the cannon
            List<Entity> living = projectileEntity.getNearbyEntities(8, 8, 8);
            //do only once
            if (i == 0) {
                confuseShooter(living, firingLoc, design.getBlastConfusion());
            }
        }

        //check if the temperature exceeds the limit and overloading
        if (cannon.checkHeatManagement() || cannon.isExplodedDueOverloading()) {
            CannonManager.getInstance().removeCannon(cannon, true, true, BreakCause.Overheating);
            return;
        }

        //reset after firing
        cannon.setLastFired(System.currentTimeMillis());
        cannon.setSoot(cannon.getSoot() + design.getSootPerGunpowder() * cannon.getLoadedGunpowder());

        if (!removeCharge) {
            return;
        }

        cannon.setProjectilePushed(design.getProjectilePushing());

        plugin.logDebug("fire event complete, charge removed from the cannon");
        //removes the gunpowder and projectile loaded in the cannon if not set otherwise
        if (design.isRemoveChargeAfterFiring())
            cannon.removeCharge();
    }

    /**
     * creates a imitated explosion made of blocks which is transmitted to shooter in a give distance
     *
     */
    public void muzzleFire() {
        var config = plugin.getMyConfig();
        double minDist = config.getImitatedBlockMinimumDistance();
        double maxDist = config.getImitatedBlockMaximumDistance();
        float maxVol = config.getImitatedSoundMaximumVolume();
        Location loc = cannon.getMuzzle();

        //simple particle effects for close distance
        //loc.getWorld().createExplosion(loc, 0F, false);
        loc.getWorld().spawnParticle(XParticle.EXPLOSION.get(), loc, 1);
        //fake blocks effects for far distance
        if (!config.isImitatedFiringEffectEnabled()) {
            return;
        }

        int maxSoundDist = config.getImitatedSoundMaximumDistance();
        SoundUtils.imitateSound(loc, cannon.getCannonDesign().getSoundFiring(), maxSoundDist, maxVol);

        List<Player> players = new ArrayList<>();
        for (Player p : loc.getWorld().getPlayers()) {
            Location pl = p.getLocation();
            double distance = pl.distance(loc);

            if (distance >= minDist && distance <= maxDist) {
                players.add(p);
            }
        }
        imitateSmoke(players);
    }

    /**
     * creates a sphere of fake block and sends it to the given shooter
     *
     */
    public void imitateSmoke(List<Player> players) {
        var config = plugin.getMyConfig();
        if (!config.isImitatedFiringEffectEnabled())
            return;

        Vector aimingVector = cannon.getAimingVector().clone();
        Location loc = cannon.getMuzzle();

        double duration = config.getImitatedFiringTime();

        for (Player name : players) {
            //make smoke and fire effects for large distance
            FakeBlockHandler.getInstance().imitateLine(name, loc, aimingVector, 0, 1, config.getImitatedFireMaterial(), FakeBlockType.MUZZLE_FIRE, duration);
            FakeBlockHandler.getInstance().imitatedSphere(name, loc.clone().add(aimingVector.clone().normalize()), 2, config.getImitatedSmokeMaterial(), FakeBlockType.MUZZLE_FIRE, duration);
        }
    }

    /**
     * confuses an entity to simulate the blast of a cannon
     *
     * @param living      entity to confuse
     * @param firingLoc   distance to the muzzle
     * @param confuseTime how long the enitity is confused in seconds
     */
    private void confuseShooter(List<Entity> living, Location firingLoc, double confuseTime) {
        //confuse shooter if he wears no helmet (only for one projectile and if its configured)
        if (confuseTime <= 0) {
            return;
        }

        PotionEffect confusionEffect = new PotionEffect(XPotion.NAUSEA.get(), (int) confuseTime * 20, 0);
        for (Entity next : living) {
            if (isHarmEntity(next)) {//damage living entities and unprotected players
                LivingEntity livingEntity = (LivingEntity) next;
                if (livingEntity.getLocation().distance(firingLoc) < 5.0)
                    livingEntity.damage(1);
                livingEntity.addPotionEffect(confusionEffect);
            }
        }
    }

    private boolean isHarmEntity(Entity next) {
        if (next instanceof Player player) {
            //if shooter has no helmet and is not in creative and there are confusion effects - harm him
            return player.isOnline() && !CheckHelmet(player) && player.getGameMode() != GameMode.CREATIVE;
        }

        return next instanceof LivingEntity;
    }

    private boolean CheckHelmet(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        return helmet != null;
    }
}
