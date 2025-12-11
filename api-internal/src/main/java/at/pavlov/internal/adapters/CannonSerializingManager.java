package at.pavlov.internal.adapters;

import at.pavlov.internal.Key;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

public class CannonSerializingManager {
    public static final TypeSerializerCollection serializerCollection = TypeSerializerCollection.defaults().childBuilder()
            .register(Key.class, new KeySerializer())
            .register(CustomProjectileDefinition.class, new CustomProjectileDefinitionSerializer())
            .build();
}
