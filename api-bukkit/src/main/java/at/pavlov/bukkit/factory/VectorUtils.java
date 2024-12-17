package at.pavlov.bukkit.factory;

import at.pavlov.internal.container.location.CannonVector;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class VectorUtils {
    public static Location toLoc(World world, CannonVector vector) {
        if ( vector == null) {
            return null;
        }

        return new Location(world, vector.getX(), vector.getY(), vector.getZ());
    }

    public static CannonVector fromLoc(Location location) {
        if (location == null) {
            return null;
        }

        return new CannonVector(location.getX(), location.getY(), location.getZ());
    }

    public static Vector toBaseVector(CannonVector vector) {
        if (vector == null) {
            return null;
        }

        return new Vector(vector.getX(), vector.getY(), vector.getZ());
    }

    public static CannonVector fromBaseVector(Vector vector) {
        if (vector == null) {
            return null;
        }

        return new CannonVector(vector.getX(), vector.getY(), vector.getZ());
    }
}
