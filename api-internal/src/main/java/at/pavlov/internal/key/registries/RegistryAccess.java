package at.pavlov.internal.key.registries;

import at.pavlov.internal.key.Key;
import at.pavlov.internal.key.KeyHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RegistryAccess<T extends KeyHolder> {
    boolean has(@NotNull Key key);

    default boolean has(@NotNull String key) {
        return has(Key.from(key));
    }

    @NotNull T of(@Nullable Key key, @NotNull T defaultValue);

    default @NotNull T of(@Nullable String string, @NotNull T defaultValue) {
        if (string == null) {
            return defaultValue;
        }

        return of(Key.from(string), defaultValue);
    }

    @Nullable T of(@Nullable Key key);

    default @Nullable T of(@Nullable String string) {
        if (string == null) {
            return null;
        }

        return of(Key.from(string));
    }
}
