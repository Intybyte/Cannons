package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.cannon.Cannon;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MovecraftCannonTracker {
    private static final Map<UUID, Set<Cannon>> cannonMap = new HashMap<>();

    public static Set<Cannon> getCannons(UUID uuid) {
        return cannonMap.get(uuid);
    }

    public static void setCannons(UUID craftId, Set<Cannon> cannons) {
        cannonMap.put(craftId, cannons);
    }

    public static void clearCannons(UUID craftId) {
        cannonMap.remove(craftId);
    }
}
