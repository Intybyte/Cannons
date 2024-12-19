package at.pavlov.cannons;

import at.pavlov.bukkit.container.BukkitTarget;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import org.bukkit.Location;

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
        CannonVector vector = VectorUtils.fromLoc(center);

        for (BukkitTarget target : targets.values()) {
            if (target.centerLocation().getVector().distanceSquared(vector) < radius * radius)
                newTargetList.add(target);
        }
        return newTargetList;
    }

    public static HashSet<BukkitTarget> getTargetsInBox(Location center, double lengthX, double lengthY, double lengthZ){
        HashSet<BukkitTarget> newTargetList = new HashSet<>();
        CannonVector vector = VectorUtils.fromLoc(center);

        for (BukkitTarget target : targets.values()) {
            Coordinate newLoc = target.centerLocation();
            CannonVector box = newLoc.getVector().subtract(vector);

            if (Objects.equals(newLoc.getWorld(), center.getWorld()) && Math.abs(box.getX())<lengthX/2 && Math.abs(box.getY())<lengthY/2 && Math.abs(box.getZ())<lengthZ/2)
                newTargetList.add(target);
        }
        return newTargetList;
    }
}
