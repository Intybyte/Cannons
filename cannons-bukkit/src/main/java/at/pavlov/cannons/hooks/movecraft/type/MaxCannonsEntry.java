package at.pavlov.cannons.hooks.movecraft.type;

import lombok.Getter;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class MaxCannonsEntry implements CannonCheck {
    private final List<String> names;
    private final double max;
    private final boolean numericMax;

    public MaxCannonsEntry(List<String> names, @NotNull Pair<Boolean, ? extends Number> max) {
        this.names = names;
        this.max = max.getRight().doubleValue();
        this.numericMax = max.getLeft();
    }

    /**
     *
     * @return Empty if no error, otherwise return the error
     */
    public Optional<String> detect(int count, int size) {
        if (numericMax) {
            if (count > max)
                return Optional.of(String.format("You have too many %s cannon types : %d > %d", allNames(), count, (int) max));
        } else {
            double blockPercent = 100D * count / size;
            if (blockPercent > max)
                return Optional.of(String.format("You have too many %s cannon types : %.2f%% > %.2f%%", allNames(), blockPercent, max));
        }

        return Optional.empty();
    }

    public String allNames() {
        return String.join(", ", names);
    }

    @Override
    public Optional<String> check(Craft craft, Map<String, Integer> cannonCountMap) {
        int count = 0;
        for (String name : names) {
            count += cannonCountMap.getOrDefault(name, 0);
        }

        return detect(count, craft.getOrigBlockCount());
    }
}
