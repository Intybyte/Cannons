package adapters;

import at.pavlov.internal.Key;
import at.pavlov.internal.adapters.CannonSerializingManager;
import at.pavlov.internal.projectile.definition.DefaultProjectileDefinition;
import org.junit.jupiter.api.Test;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BuilderTypeSerializerTest {

    @Test
    void testBuilderSerialization() throws Exception {
        // Create temp file for YAML loader
        Path tempFile = Files.createTempFile("test", ".yml");

        // Create YAML loader with custom serializers
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(tempFile)
                .defaultOptions(options -> options.serializers(CannonSerializingManager.serializerCollection))
                .build();

        // Create an instance using the builder
        DefaultProjectileDefinition original = DefaultProjectileDefinition.builder()
                .key(Key.mc("test"))
                .drag(2.1)
                .constantAcceleration(1.2)
                .waterDrag(2.4)
                .gravity(-1.4)
                .build();

        // Serialize to a ConfigurationNode
        ConfigurationNode node = loader.createNode();
        node.set(original);

        // Optionally, print YAML for debugging
        System.out.println(node);

        // Deserialize back
        DefaultProjectileDefinition deserialized = node.get(DefaultProjectileDefinition.class);

        // Basic assertions
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original, deserialized, "Deserialized object should be equal to original");
    }
}
