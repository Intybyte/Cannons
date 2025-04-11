package at.pavlov.cannons.utils;

import java.util.function.Consumer;

public class TimeUtils {
    private TimeUtils() {}

    public static void testTime(Runnable tested, Consumer<String> printer, String cause) {
        long start = System.currentTimeMillis();
        tested.run();
        long end = System.currentTimeMillis();
        long total = start - end;
        printer.accept("Execution of \"" + cause + "\" completed in " + total + " ms");
    }
}
