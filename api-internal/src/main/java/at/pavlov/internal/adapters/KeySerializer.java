package at.pavlov.internal.adapters;

import at.pavlov.internal.Key;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class KeySerializer implements TypeSerializer<Key> {

    @Override
    public Key deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        String value = node.getString();
        if (value == null || !value.contains(":")) {
            throw new SerializationException("Invalid Key format, expected namespace:key");
        }
        String[] parts = value.split(":", 2);
        return new Key(parts[0], parts[1]);
    }

    @Override
    public void serialize(@NotNull Type type, Key obj, @NotNull ConfigurationNode node) throws SerializationException {
        if (obj == null) return;
        node.set(obj.full());
    }
}
