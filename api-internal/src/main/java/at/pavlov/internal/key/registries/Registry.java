package at.pavlov.internal.key.registries;

import at.pavlov.internal.Key;
import at.pavlov.internal.key.KeyHolder;
import at.pavlov.internal.key.registries.exceptions.RegistryDuplicate;
import at.pavlov.internal.key.registries.exceptions.RegistryFrozen;
import at.pavlov.internal.key.registries.exceptions.RegistryValidator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@NoArgsConstructor
public class Registry<T extends KeyHolder> implements RegistryAccess<T> {
    protected final Map<Key, T> map = new HashMap<>();
    @Setter
    @Getter
    protected boolean frozen = false;
    @Setter
    protected @NotNull RegistryValidator<? super T> validator = (key, value) -> {};

    public static class Composite<T extends KeyHolder> implements RegistryAccess<T> {
        protected final List<Registry<? extends T>> list = new ArrayList<>();

        @SafeVarargs
        public Composite(Registry<? extends T>... args) {
            list.addAll(Arrays.asList(args));
        }

        @Override
        public boolean has(@NotNull Key key) {
            for (var registry : list) {
                if (registry.map.containsKey(key)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public @NotNull T of(@Nullable Key key, @NotNull T defaultValue) {
            if (key == null) {
                return defaultValue;
            }

            for (var registry : list) {
                T value = registry.map.get(key);
                if (value != null) {
                    return value;
                }
            }

            return defaultValue;
        }

        @Override
        public @Nullable T of(@Nullable Key key) {
            if (key == null) {
                return null;
            }

            for (var registry : list) {
                T value = registry.map.get(key);
                if (value != null) {
                    return value;
                }
            }

            return null;
        }
    }

    public Registry(Supplier<Collection<@NotNull T>> args) {
        Collection<@NotNull T> arguments = args.get();
        for (T arg : arguments) {
            register(arg);
        }
    }

    @Override
    public boolean has(@NotNull Key key) {
        return map.containsKey(key);
    }

    @Override
    public @NotNull T of(@Nullable Key key, @NotNull T defaultValue) {
        if (key == null) {
            return defaultValue;
        }

        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public @Nullable T of(@Nullable Key key) {
        if (key == null) {
            return null;
        }

        return map.get(key);
    }

    @SafeVarargs
    public final void register(@NotNull T... entries) {
        if (frozen) {
            throw new RegistryFrozen("Can't register key for frozen registry");
        }

        for (T entry : entries) {
            Key key = entry.getKey();
            if (map.containsKey(key)) {
                throw new RegistryDuplicate("Duplicate key in registry of class: " + entry.getClass());
            }

            try {
                validator.test(key, entry);
                map.put(key, entry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(Supplier<@NotNull T> entry) {
        if (frozen) {
            throw new RegistryFrozen("Can't register key for frozen registry");
        }

        T result = entry.get();
        Key key = result.getKey();
        validator.test(key, result);

        if (map.containsKey(key)) {
            throw new RegistryDuplicate("Duplicate key in registry of class: " + entry.getClass());
        }

        map.put(key, result);
    }

    public void clear() {
        map.clear();
    }
}
