package at.pavlov.cannons.hooks.movecraft.type.properties;

import at.pavlov.cannons.Cannons;
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

public class PropertyWrapper<T> {
    public static final Set<CraftType> notifyError = new HashSet<>();

    public final NamespacedKey key;
    public final Class<T> clazz;
    public final Supplier<? extends T> errorFallback;
    public final String fileEntryName;

    public PropertyWrapper(NamespacedKey key, Class<T> clazz, Supplier<? extends T> errorFallback) {
        this.key = key;
        this.clazz = clazz;
        this.errorFallback = errorFallback;
        this.fileEntryName = PropertyUtils.snakeToCamel(key.getKey());
    }

    public void register(@NotNull QuadFunction<TypeData, CraftType, String, NamespacedKey, T> loadProvider) {
        register(loadProvider, null);
    }

    public void register(@NotNull QuadFunction<TypeData, CraftType, String, NamespacedKey, T> loadProvider, @Nullable Function<CraftType, Object> defaultProvider) {
        if (defaultProvider == null) {
            CraftType.registerProperty(new ObjectPropertyImpl(fileEntryName, key, new PropertyUtils.ShutUp<>(loadProvider)));
        } else {
            CraftType.registerProperty(new ObjectPropertyImpl(fileEntryName, key, new PropertyUtils.ShutUp<>(loadProvider), new PropertyUtils.ShutUp2<>(defaultProvider)));
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
                "Failed to get " + fileEntryName +" property from craft " +
                    type.getStringProperty(CraftType.NAME) +
                    " so it won't be applied. - Cause: " +
                    exception.getMessage()
            );
            return errorFallback.get();
        }
    }
}
