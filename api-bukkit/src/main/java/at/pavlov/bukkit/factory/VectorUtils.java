package at.pavlov.bukkit.factory;

import at.pavlov.internal.container.location.CannonVector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class VectorUtils {
    public static Location toLoc(World world, CannonVector vector) {
        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector toBaseVector(CannonVector vector) {
        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static CannonVector fromBaseVector(Vector vector) {
        return new CannonVector(vector.getX(), vector.getY(), vector.getZ());
    }
}
