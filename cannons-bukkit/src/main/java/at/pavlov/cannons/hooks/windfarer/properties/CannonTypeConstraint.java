package at.pavlov.cannons.hooks.windfarer.properties;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.TypeData;
import net.countercraft.movecraft.util.Pair;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SerializableAs("CannonsRevamped_CannonTypeConstraint")
public record CannonTypeConstraint(
        List<String> cannonTypes,
        boolean numericMin,
        Optional<Double> min,
        boolean numericMax,
        Optional<Double> max
) implements ConfigurationSerializable {

    public CannonTypeConstraint(CannonTypeConstraint toCopy) {
        this(new ArrayList(toCopy.cannonTypes()), toCopy.numericMin, Optional.ofNullable(toCopy.min().get()), toCopy.numericMax, Optional.ofNullable(toCopy.max().get()));
    }

    protected Optional<String> checkMin(double blockPercent, int count) {
        final double minD = min.get();
        if (numericMax) {
            if (count < minD)
                return Optional.of(String.format("You have too few %s cannon types : %d < %d", allNames(), count, (int) minD));
        } else {
            if (blockPercent < minD)
                return Optional.of(String.format("You have too few %s cannon types : %.2f%% < %.2f%%", allNames(), blockPercent, minD));
        }
        return Optional.empty();
    }

    protected Optional<String> checkMax(double blockPercent, int count) {
        final double maxD = max.get();
        if (numericMax) {
            if (count > maxD)
                return Optional.of(String.format("You have too many %s cannon types : %d > %d", allNames(), count, (int) maxD));
        } else {
            if (blockPercent > maxD)
                return Optional.of(String.format("You have too many %s cannon types : %.2f%% > %.2f%%", allNames(), blockPercent, maxD));
        }
        return Optional.empty();
    }

    /**
     *
     * @return Empty if no error, otherwise return the error
     */
    public Optional<String> callOnDetection(int count, int size) {
        final double blockPercent = 100D * count / size;
        if (min.isPresent()) {
            Optional<String> tempResult = this.checkMin(blockPercent, count);
            if (tempResult.isPresent()) {
                return tempResult;
            }
        }

        if (max.isPresent()) {
            Optional<String> tempResult = this.checkMax(blockPercent, count);
            if (tempResult.isPresent()) {
                return tempResult;
            }
        }

        return Optional.empty();
    }

    public String allNames() {
        return String.join(", ", cannonTypes);
    }

    public Optional<String> check(Craft craft, Map<String, Integer> cannonCountMap) {
        int count = 0;
        for (String name : cannonTypes) {
            count += cannonCountMap.getOrDefault(name, 0);
        }

        return callOnDetection(count, craft.getOrigBlockCount());
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return Map.of(
                "cannonTypes", this.allNames(),
                "min", this.numericMin ? TypeData.NUMERIC_PREFIX : "" + this.min,
                "max", this.numericMax ? TypeData.NUMERIC_PREFIX : "" + this.max
        );
    }

    // Copied over code from Windfarer and Movecraft
    // Tries to parse a given input of the format N1234 or 123.456
    // If the "numeric" format is detected, Left will be true, otherwise false
    static Pair<Boolean, ? extends Number> parseLimit(@NotNull Object input) {
        if (input == null) {
            return null;
        }
        if (input instanceof String str) {
            if (str.contains(TypeData.NUMERIC_PREFIX)) {
                String[] parts = str.split(TypeData.NUMERIC_PREFIX);
                int val = Integer.parseInt(parts[1]);
                return new Pair<>(true, val);
            }
            else
                return new Pair<>(false, Double.valueOf(str));
        }
        else if (input instanceof Integer intTmp) {
            return new Pair<>(false, intTmp);
        }
        else
            return new Pair<>(false, (double) input);
    }

    public static @NotNull CannonTypeConstraint deserialize(@NotNull Map<String, Object> yamlData) {
        List<String> cannonTypes = new ArrayList<>();
        Object listRaw = yamlData.getOrDefault("cannonTypes", "");

        switch(listRaw) {
            case String rawString -> {
                // Split all the strings in "cannonTypes" apart by ",", then add them all to our cannonTypeList
                cannonTypes.addAll(Arrays.asList(rawString.split(",")));
            }
            case List<?> rawList -> {
                for (Object object : rawList) {
                    if (object instanceof String stringTmp) {
                        cannonTypes.add(stringTmp);
                    }
                }
            }
            default -> {
                // NOt relevant here, ignored
                // Added anyway so the compiler is happy
            }
        };

        Pair<Boolean, ? extends Number> min = parseLimit(yamlData.getOrDefault("min", null));
        Pair<Boolean, ? extends Number> max = parseLimit(yamlData.getOrDefault("max", null));

        return new CannonTypeConstraint(
            cannonTypes,
            min != null ? min.getLeft() : false, // If present, use numeric min, otherwise dont
            Optional.ofNullable(min != null ? min.getRight().doubleValue() : null),
            max != null ? max.getLeft() : false, // If present, use numeric max, otherwise dont
            Optional.ofNullable(max != null ? max.getRight().doubleValue() : null)
        );
    }
}
