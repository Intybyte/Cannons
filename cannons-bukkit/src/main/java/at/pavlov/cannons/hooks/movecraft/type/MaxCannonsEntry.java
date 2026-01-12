package at.pavlov.cannons.hooks.movecraft.type;

import lombok.Getter;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

@Getter
public class MaxCannonsEntry implements CannonCheck {
    private final String name;
    private final double max;
    private final boolean numericMax;

    public MaxCannonsEntry(String name, @NotNull Pair<Boolean, ? extends Number> max) {
        this.name = name;
        this.max = max.getRight().doubleValue();
        this.numericMax = max.getLeft();
    }

    public boolean check(int count, int size) {
        if (numericMax) {
            return !(count > max);
        }

        double percent = 100D * count / size;
        return !(percent > max);
    }

    /**
     *
     * @return Empty if no error, otherwise return the error
     */
    public Optional<String> detect(int count, int size) {
        if (numericMax) {
            if (count > max)
                return Optional.of(String.format("You have too many %s cannon types : %d > %d", name, count, (int) max));
        } else {
            double blockPercent = 100D * count / size;
            if (blockPercent > max)
                return Optional.of(String.format("You have too many %s cannon types : %.2f%% > %.2f%%", name, blockPercent, max));
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> check(Craft craft, Map<String, Integer> cannonCountMap) {
        Integer count = cannonCountMap.get(name);
        if (count == null) return Optional.empty();
        return detect(count, craft.getOrigBlockCount());
    }
}
