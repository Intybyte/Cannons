package at.pavlov.cannons.hooks.movecraft.type;

import at.pavlov.cannons.Cannons;
import lombok.AllArgsConstructor;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.TypeData;
import net.countercraft.movecraft.craft.type.property.ObjectPropertyImpl;
import net.countercraft.movecraft.util.functions.QuadFunction;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

@AllArgsConstructor
public class PropertyWrapper<T> {
    public static final Set<CraftType> notifyError = new HashSet<>();

    public final NamespacedKey key;
    public final Class<T> clazz;
    public final Supplier<? extends T> errorFallback;

    private static String snakeToCamel(String input) {
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

    private record ShutUp<T>(QuadFunction<TypeData, CraftType, String, NamespacedKey, T> otherType) implements QuadFunction<TypeData, CraftType, String, NamespacedKey, Object> {
        @Override
        public Object apply(TypeData typeData, CraftType craftType, String s, NamespacedKey namespacedKey) {
            return otherType.apply(typeData, craftType, s, namespacedKey);
        }
    }

    private record ShutUp2<T>(Function<CraftType, T> otherType) implements Function<CraftType, Object> {
        @Override
        public Object apply(CraftType craftType) {
            return otherType.apply(craftType);
        }
    }

    public void register(@NotNull QuadFunction<TypeData, CraftType, String, NamespacedKey, T> loadProvider) {
        register(loadProvider, null);
    }

    public void register(@NotNull QuadFunction<TypeData, CraftType, String, NamespacedKey, T> loadProvider, @Nullable Function<CraftType, Object> defaultProvider) {
        String configName = snakeToCamel(key.getKey());
        if (defaultProvider == null) {
            CraftType.registerProperty(new ObjectPropertyImpl(configName, key, new ShutUp<>(loadProvider)));
        } else {
            CraftType.registerProperty(new ObjectPropertyImpl(configName, key, new ShutUp<>(loadProvider), new ShutUp2<>(defaultProvider)));
        }
    }

    public T get(CraftType type) {
        try {
            Object objectProperty = type.getObjectProperty(key);
            if (clazz.isInstance(objectProperty)) {
                return clazz.cast(objectProperty);
            } else {
                throw new IllegalStateException("maxCannons must be a set.");
            }
        } catch (Exception exception) {
            notifyError.add(type);
            Cannons.getPlugin().logSevere(
                "Failed to get maxCannons property from craft " +
                    type.getStringProperty(CraftType.NAME) +
                    " maxCannons won't be applied. - Cause: " +
                    exception.getMessage()
            );
            return errorFallback.get();
        }
    }
}
