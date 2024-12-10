package at.pavlov.cannons;

import at.pavlov.cannons.container.Target;
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
    private static HashMap<UUID, Target> targets = new HashMap<>();

    public static void addTarget(Target target){
        targets.put(target.uniqueId(), target);
    }

    public static Target getTarget(UUID uid){
        return targets.get(uid);
    }

    public static HashSet<Target> getTargetsInSphere(Location center, double radius){
        HashSet<Target> newTargetList = new HashSet<>();

        for (Target target : targets.values()) {
            if (target.centerLocation().distanceSquared(center) < radius * radius)
                newTargetList.add(target);
        }
        return newTargetList;
    }

    public static HashSet<Target> getTargetsInBox(Location center, double lengthX, double lengthY, double lengthZ){
        HashSet<Target> newTargetList = new HashSet<>();

        for (Target target : targets.values()) {
            Location newLoc = target.centerLocation();
            Vector box = newLoc.subtract(center).toVector();

            if (Objects.equals(newLoc.getWorld(), center.getWorld()) && Math.abs(box.getX())<lengthX/2 && Math.abs(box.getY())<lengthY/2 && Math.abs(box.getZ())<lengthZ/2)
                newTargetList.add(target);
        }
        return newTargetList;
    }
}
