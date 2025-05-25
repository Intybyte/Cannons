package mockbukkit;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.stream.Stream;

public class EmptyRegistry<T extends Keyed> implements Registry<T> {
    @Override
    public @Nullable T get(@NotNull NamespacedKey namespacedKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull T getOrThrow(@NotNull NamespacedKey namespacedKey) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Stream<T> stream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        throw new UnsupportedOperationException();
    }
}
