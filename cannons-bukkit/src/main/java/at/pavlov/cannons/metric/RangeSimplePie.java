package at.pavlov.cannons.metric;

import org.bstats.charts.SimplePie;

import java.util.function.Supplier;

public class RangeSimplePie extends SimplePie {
    public RangeSimplePie(String chartId, Supplier<Integer> callable) {
        super(chartId, () -> getRange(callable.get()));
    }

    private static String getRange(int amount) {
        int range = amount - (amount % 10);
        return String.valueOf(range);
    }
}
