package at.pavlov.internal.key.registries;

import at.pavlov.internal.key.Key;
import at.pavlov.internal.key.KeyHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Registry<T extends KeyHolder> {
    private final Map<Key, T> map = new HashMap<>();

    public Registry(Supplier<Collection<@NotNull T>> args) {
        Collection<@NotNull T> arguments = args.get();
        for (T arg : arguments) {
            register(arg);
        }
    }

    public @Nullable T of(@Nullable Key key) {
        if (key == null) {
            return null;
        }

        return map.get(key);
    }

    public @Nullable T of(@Nullable String string) {
        if (string == null) {
            return null;
        }

        return of(Key.from(string));
    }

    @SafeVarargs
    public final void register(@NotNull T... entries) {
        for (T entry : entries) {
            if (map.containsKey(entry.getKey())) {
                throw new RegistryDuplicate("Duplicate key in registry of class: " + entry.getClass());
            }

            map.put(entry.getKey(), entry);
        }
    }

    public void register(Supplier<@NotNull T> entry) {
        T result = entry.get();
        if (map.containsKey(result.getKey())) {
            throw new RegistryDuplicate("Duplicate key in registry of class: " + entry.getClass());
        }

        map.put(result.getKey(), result);
    }
}
