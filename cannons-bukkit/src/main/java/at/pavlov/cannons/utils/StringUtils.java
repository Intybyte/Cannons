package at.pavlov.cannons.utils;

import java.util.Iterator;
import java.util.Objects;

public class StringUtils {
    public static String join(Iterable<String> iterable, String separator) {
        return iterable != null ? join(iterable.iterator(), separator) : null;
    }

    public static String join(Iterator<String> iterator, String separator) {
        if (iterator == null) {
            return null;
        }

        if (!iterator.hasNext()) {
            return "";
        }

        var sep = Objects.toString(separator, "");
        var result = new StringBuilder();

        result.append(Objects.toString(iterator.next(), ""));
        while (iterator.hasNext()) {
            result.append(sep);
            result.append(Objects.toString(iterator.next(), ""));
        }

        return result.toString();
    }
}
