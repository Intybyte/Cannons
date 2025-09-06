package at.pavlov.internal;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

public record Key(String namespace, String key) {
    public static final Pattern NAMESPACED_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+:[a-z0-9._-]+$");

    public Key(String namespace, String key) {
        this.namespace = namespace.toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);

        if (!NAMESPACED_KEY_PATTERN.matcher(this.full()).matches()) {
            throw new IllegalArgumentException("Invalid NS key: " + this.full());
        }
    }

    @ApiStatus.Internal
    public static Key cannons(String key) {
        return new Key("cannons", key);
    }

    public static Key mc(String key) {
        return new Key("minecraft", key);
    }

    public static Key from(String compositeKey) {
        var strings = compositeKey.split(":");
        if (strings.length >= 3) {
            throw new IllegalArgumentException("Invalid NS key: " + compositeKey);
        }

        if (strings.length == 1) {
            return Key.mc(strings[0].trim().toLowerCase());
        }

        return new Key(strings[0].trim().toLowerCase(), strings[1].trim().toLowerCase());
    }

    public static Collection<Key> from(Collection<String> collection) {
        return collection.stream().map(Key::from).toList();
    }

    public String full() {
        return namespace + ":" + key;
    }

    public boolean matches(String s) {
        return this.full().equals(s);
    }

    @Override
    public @NotNull String toString() {
        return "Key[" +
                "namespace=" + namespace + ", " +
                "key=" + key + ']';
    }
}
