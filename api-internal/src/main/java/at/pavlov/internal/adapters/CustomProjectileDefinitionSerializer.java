package at.pavlov.internal.adapters;

import at.pavlov.internal.Key;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class CustomProjectileDefinitionSerializer implements TypeSerializer<CustomProjectileDefinition> {

    @Override
    public CustomProjectileDefinition deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        // Use the node's key as the projectile key
        Key key = Key.from(node.key().toString()); // top-level key
        Key entityKey = Key.from(node.node("entity").getString("SNOWBALL"));

        // Get default projectile physics if entityKey not registered
        ProjectilePhysics dpd = Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.of(entityKey);
        if (dpd == null) {
            dpd = ProjectilePhysics.DEFAULT;
        }

        // Build the CustomProjectileDefinition
        return CustomProjectileDefinition.builder()
                .key(key)
                .entityKey(entityKey)
                .constantAcceleration(node.node("constantAcceleration").get(Double.class, dpd.getConstantAcceleration()))
                .gravity(node.node("gravity").getDouble(dpd.getGravity()))
                .drag(node.node("drag").getDouble(dpd.getDrag()))
                .waterDrag(node.node("waterDrag").getDouble(dpd.getWaterDrag()))
                .glowing(node.node("glowing").getBoolean(false))
                .onFire(node.node("onFire").getBoolean(false))
                .charged(node.node("charged").getBoolean(false))
                .critical(node.node("critical").getBoolean(false))
                .material(Key.from(node.node("material").getString("SNOWBALL")))
                .customModelData(node.node("customModelData").get(Integer.class, (Integer) null))
                .build();
    }

    @Override
    public void serialize(@NotNull Type type, CustomProjectileDefinition obj, @NotNull ConfigurationNode node) throws SerializationException {
        // Not needed, can throw UnsupportedOperationException if you never serialize
        throw new UnsupportedOperationException("Serialization not supported for this deserializer");
    }
}

