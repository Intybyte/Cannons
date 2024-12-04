package at.pavlov.cannons.hooks.movecraft.type;

import lombok.Getter;
import net.countercraft.movecraft.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MaxCannonsEntry {
    @Getter
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
                return Optional.of(String.format("%d > %d", count, (int) max));
        } else {
            double blockPercent = 100D * count / size;
            if (blockPercent > max)
                return Optional.of(String.format("%.2f%% > %.2f%%", blockPercent, max));
        }

        return Optional.empty();
    }
}
