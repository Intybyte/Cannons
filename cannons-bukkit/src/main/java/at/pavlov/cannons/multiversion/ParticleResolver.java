package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.Cannons;
import org.bukkit.Particle;

import java.lang.reflect.Field;

public class ParticleResolver {
    private static final int[] version = VersionHandler.getVersion();
    private static Particle explosion;

    static {
        initExplosion();
    }

    private ParticleResolver() {}

    private static void initExplosion() {
        if (version[1] >= 20) {
            explosion = Particle.EXPLOSION;
        } else {
            try {
                Field field = Particle.class.getDeclaredField("EXPLOSION_NORMAL");
                field.setAccessible(true);
                explosion = (Particle) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    public static Particle getExplosion() {
        return explosion;
    }
}
