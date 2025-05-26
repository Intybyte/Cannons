import at.pavlov.cannons.container.SpawnEntityHolder;
import mockbukkit.RegistryAccessMock;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


// Example data
// 'AREA_EFFECT_CLOUD 1-1 {Particle:"entity_effect",Radius:5f,Duration:300,Color:16711680,Effects:[{Id:2b,Amplifier:3b,Duration:300,ShowParticles:0b},{Id:7b,Amplifier:1b,Duration:20,ShowParticles:0b},{Id:9b,Amplifier:2b,Duration:300,ShowParticles:0b},{Id:19b,Amplifier:2b,Duration:300,ShowParticles:0b}]}'
public class SpawnEntityHolderTest {
    @BeforeEach
    public void setupBukkitLoggerMock() {
        Server mockServer = mock(Server.class);
        Logger mockLogger = Logger.getLogger("TEST");

        RegistryAccessMock mockRegistry = new RegistryAccessMock();

        when(mockServer.getLogger()).thenReturn(mockLogger);
        when(mockServer.getRegistry(any())).thenAnswer(invocation -> {
            // Return a mock Registry
            Class<? extends Keyed> clazz = invocation.getArgument(0);
            return mockRegistry.getRegistry(clazz);
        });

        setBukkitServer(mockServer);
        // Inject the mock into Bukkit

    }

    // Use reflection to set the private static Bukkit.server field
    private void setBukkitServer(Server server) {
        try {
            var field = Bukkit.class.getDeclaredField("server");
            field.setAccessible(true);
            field.set(null, server);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set Bukkit server mock", e);
        }
    }

    @Test
    public void testValidConstructor() {
        String input = "ZOMBIE 1-3 {}";
        SpawnEntityHolder holder = new SpawnEntityHolder(input);

        assertEquals(EntityType.ZOMBIE, holder.getType());
        assertEquals(1, holder.getMinAmount());
        assertEquals(3, holder.getMaxAmount());
    }

    @Test
    public void testInvalidEntityTypeDefaultsToSnowball() {
        String input = "INVALID_TYPE 1-3 {}";
        SpawnEntityHolder holder = new SpawnEntityHolder(input);

        assertEquals(EntityType.SNOWBALL, holder.getType()); // As per fallback in parseEntityType
    }

    @Test
    public void testInvalidMinMaxThrowsException() {
        SpawnEntityHolder holder = new SpawnEntityHolder("ZOMBIE invalid-range {}");
        assertEquals(0, holder.getMinAmount());
    }

    @Test
    public void testNormalData() {
        SpawnEntityHolder holder = new SpawnEntityHolder("AREA_EFFECT_CLOUD 1-1 {}");
        assertEquals(EntityType.AREA_EFFECT_CLOUD, holder.getType());
    }

    @Test
    public void testMissingDataHandled() {
        String input = "ZOMBIE 2-5";
        SpawnEntityHolder holder = new SpawnEntityHolder(input);

        assertEquals(EntityType.ZOMBIE, holder.getType());
        assertEquals(2, holder.getMinAmount());
        assertEquals(5, holder.getMaxAmount());
    }


    @Test
    public void testPotionEffectsParsing() {
        String json = """
        ZOMBIE 1-2 {
            Effects: [
                {
                    Id: "minecraft:speed",
                    Duration: "600",
                    Amplifier: "2",
                    Ambient: "1",
                    ShowParticles: "1b",
                    Icon: "0"
                }
            ]
        }
        """;


        SpawnEntityHolder holder = new SpawnEntityHolder(json);
        assertFalse(holder.getPotionEffects().isEmpty());
        assertEquals("SPEED", holder.getPotionEffects().get(0).getType().getName());

    }
}
