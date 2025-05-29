package at.pavlov.internal;

import org.jetbrains.annotations.ApiStatus;

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
            return Key.mc(strings[0]);
        }

        return new Key(strings[0], strings[1]);
    }

    public String full() {
        return namespace + ":" + key;
    }

    @Override
    public String toString() {
        return "Key[" +
                "namespace=" + namespace + ", " +
                "key=" + key + ']';
    }

}
