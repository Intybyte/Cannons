package at.pavlov.internal;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberTricks {
    public static int floor(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor - (int)(Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int ceil(double num) {
        int floor = (int)num;
        return (double)floor == num ? floor : floor + (int)(~Double.doubleToRawLongBits(num) >>> 63);
    }

    public static int round(double num) {
        return floor(num + 0.5);
    }

    public static double square(double num) {
        return num * num;
    }

    public static double constrainToRange(double value, double min, double max) {
        if (min <= max) {
            return Math.min(Math.max(value, min), max);
        } else {
            throw new IllegalArgumentException(String.format("min (%s) must be less than or equal to max (%s)", min, max));
        }
    }
}
