package at.pavlov.cannons.hooks.movecraft.type.properties;

import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import at.pavlov.cannons.hooks.movecraft.type.CraftKeys;
import at.pavlov.cannons.hooks.movecraft.type.MaxCannonsEntry;
import at.pavlov.cannons.hooks.movecraft.type.MinCannonsEntry;
import net.countercraft.movecraft.craft.type.TypeData;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CannonProperties {
    public static final PropertyWrapper<Set> MAX_CANNONS = new PropertyWrapper<>(
        CraftKeys.MAX_CANNONS,
        Set.class,
        Set::of
    );

    public static final PropertyWrapper<Set> MIN_CANNONS = new PropertyWrapper<>(
        CraftKeys.MIN_CANNONS,
        Set.class,
        Set::of
    );

    public static final PropertyWrapperInt MAX_MASS = new PropertyWrapperInt(CraftKeys.MAX_MASS, (type) -> null);
    public static final PropertyWrapperInt MIN_MASS = new PropertyWrapperInt(CraftKeys.MIN_MASS, (type) -> null);
    public static final PropertyWrapper<Set> EXCLUDE_FROM_MASS = new PropertyWrapper<>(
        CraftKeys.EXCLUDE_FROM_MASS,
        Set.class,
        Set::of
    );

    public static final PropertyWrapperBoolean USE_SHIP_ANGLES = new PropertyWrapperBoolean(CraftKeys.USE_SHIP_ANGLES, (type) -> false);

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

        MIN_CANNONS.register((data, type, fileKey, namespacedKey) -> {
            var map = data.getData(fileKey).getBackingData();
            if (map.isEmpty())
                throw new TypeData.InvalidValueException("Value for " + fileKey + " must not be an empty map");

            Set<MinCannonsEntry> maxCannons = new HashSet<>();
            for (var entry : map.entrySet()) {
                if (entry.getKey() == null)
                    throw new TypeData.InvalidValueException("Keys for " + fileKey + " must be a string cannon name.");

                Object entryKey = entry.getKey();
                var limit = MovecraftUtils.parseLimit(entry.getValue());
                maxCannons.add(new MinCannonsEntry(MovecraftUtils.parseKey(entryKey), limit));
            }
            return maxCannons;
        }, (type -> new HashSet<>()));

        MAX_MASS.register();
        MIN_MASS.register();
        EXCLUDE_FROM_MASS.register((data, type, fileKey, namespacedKey) -> {
            List<String> list = data.getStringList(fileKey);
            if (list.isEmpty())
                throw new TypeData.InvalidValueException("Value for " + fileKey + " must not be an empty list");

            return new HashSet<>(list);
        }, (type -> new HashSet<>()));

        USE_SHIP_ANGLES.register();
    }
}
