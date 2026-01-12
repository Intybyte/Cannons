package at.pavlov.cannons.hooks.movecraft.type.properties;

import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import at.pavlov.cannons.hooks.movecraft.type.CraftKeys;
import at.pavlov.cannons.hooks.movecraft.type.MaxCannonsEntry;
import net.countercraft.movecraft.craft.type.TypeData;

import java.util.HashSet;
import java.util.Set;

public class CannonProperties {
    public static final PropertyWrapper<Set> MAX_CANNONS = new PropertyWrapper<>(
        CraftKeys.MAX_CANNONS,
        Set.class,
        Set::of
    );

    public static void register() {
        MAX_CANNONS.register((data, type, fileKey, namespacedKey) -> {
            var map = data.getData(fileKey).getBackingData();
            if (map.isEmpty())
                throw new TypeData.InvalidValueException("Value for " + fileKey + " must not be an empty map");

            Set<MaxCannonsEntry> maxCannons = new HashSet<>();
            for (var entry : map.entrySet()) {
                if (entry.getKey() == null)
                    throw new TypeData.InvalidValueException("Keys for " + fileKey + " must be a string cannon name.");

                Object entryKey = entry.getKey();
                var limit = MovecraftUtils.parseLimit(entry.getValue());
                maxCannons.add(new MaxCannonsEntry(MovecraftUtils.parseKey(entryKey), limit));
            }
            return maxCannons;
        }, (type -> new HashSet<>()));
    }
}
