package at.pavlov.bukkit.factory;

import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class CoordinateUtil {
    public static Location toLoc(Coordinate coordinate) {
        CannonVector vector = coordinate.getVector();
        World world = Bukkit.getWorld(coordinate.getWorld());
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Coordinate fromLoc(Location location) {
        return new Coordinate(location.getWorld().getUID(), location.getX(), location.getY(), location.getZ());
    }
}
