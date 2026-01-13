package at.pavlov.cannons.hooks.movecraft.type.properties;

import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.TypeData;
import net.countercraft.movecraft.util.functions.QuadFunction;
import org.bukkit.NamespacedKey;

import java.util.function.Function;

public class PropertyUtils {
    public static String snakeToCamel(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (char c : input.toCharArray()) {
            if (c == '_') {
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public record ShutUp<T>(
        QuadFunction<TypeData, CraftType, String, NamespacedKey, T> otherType) implements QuadFunction<TypeData, CraftType, String, NamespacedKey, Object> {
        @Override
        public Object apply(TypeData typeData, CraftType craftType, String s, NamespacedKey namespacedKey) {
            return otherType.apply(typeData, craftType, s, namespacedKey);
        }
    }

    public record ShutUp2<T>(Function<CraftType, T> otherType) implements Function<CraftType, Object> {
        @Override
        public Object apply(CraftType craftType) {
            return otherType.apply(craftType);
        }
    }
}
