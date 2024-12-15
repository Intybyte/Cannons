package at.pavlov.cannons;

import at.pavlov.bukkit.container.BukkitTarget;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * Class used by other plugins in order to add targets
 */
public class TargetManager {
    private static HashMap<UUID, BukkitTarget> targets = new HashMap<>();

    public static void addTarget(BukkitTarget target){
        targets.put(target.uniqueId(), target);
    }

    public static BukkitTarget getTarget(UUID uid){
        return targets.get(uid);
    }

    public static HashSet<BukkitTarget> getTargetsInSphere(Location center, double radius){
        HashSet<BukkitTarget> newTargetList = new HashSet<>();

        for (BukkitTarget target : targets.values()) {
            if (target.centerLocation().distanceSquared(center) < radius * radius)
                newTargetList.add(target);
        }
        return newTargetList;
    }

    public static HashSet<BukkitTarget> getTargetsInBox(Location center, double lengthX, double lengthY, double lengthZ){
        HashSet<BukkitTarget> newTargetList = new HashSet<>();

        for (BukkitTarget target : targets.values()) {
            Location newLoc = target.centerLocation();
            Vector box = newLoc.subtract(center).toVector();

            if (Objects.equals(newLoc.getWorld(), center.getWorld()) && Math.abs(box.getX())<lengthX/2 && Math.abs(box.getY())<lengthY/2 && Math.abs(box.getZ())<lengthZ/2)
                newTargetList.add(target);
        }
        return newTargetList;
    }
}
