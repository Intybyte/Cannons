package at.pavlov.internal.registries;

import at.pavlov.internal.Key;
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
            map.put(entry.key(), entry);
        }
    }

    public void register(Supplier<@NotNull T> entry) {
        T result = entry.get();
        map.put(result.key(), result);
    }
}
