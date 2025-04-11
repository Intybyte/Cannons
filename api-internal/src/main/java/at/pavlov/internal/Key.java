package at.pavlov.internal;

import org.jetbrains.annotations.ApiStatus;

public record Key(String namespace, String key) {
    @ApiStatus.Internal
    public static Key cannons(String key) {
        return new Key("cannons", key);
    }

    public static Key from(String compositeKey) {
        var strings = compositeKey.split(":");
        if (strings.length != 2) {
            throw new IllegalArgumentException("Invalid NS key: " + compositeKey);
        }

        return new Key(strings[0], strings[1]);
    }
}
