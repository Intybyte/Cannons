package at.pavlov.cannons.hooks.movecraft.type;

import lombok.Getter;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
public class MinCannonsEntry implements CannonCheck {
    private final List<String> names;
    private final double min;
    private final boolean numericMax;

    public MinCannonsEntry(List<String> names, @NotNull Pair<Boolean, ? extends Number> min) {
        this.names = names;
        this.min = min.getRight().doubleValue();
        this.numericMax = min.getLeft();
    }

    /**
     *
     * @return Empty if no error, otherwise return the error
     */
    public Optional<String> detect(int count, int size) {
        if (numericMax) {
            if (count < min)
                return Optional.of(String.format("You have too few %s cannon types : %d < %d", allNames(), count, (int) min));
        } else {
            double blockPercent = 100D * count / size;
            if (blockPercent < min)
                return Optional.of(String.format("You have too few %s cannon types : %.2f%% < %.2f%%", allNames(), blockPercent, min));
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
