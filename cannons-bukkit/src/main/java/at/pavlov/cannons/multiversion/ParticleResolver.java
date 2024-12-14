package at.pavlov.cannons.multiversion;

import org.bukkit.Particle;

public class ParticleResolver {
    private static final int[] version = VersionHandler.getVersion();
    private static Particle explosion;

    static {
        initExplosion();
    }

    private ParticleResolver() {}

    private static void initExplosion() {
        if (version[1] == 20 && version[2] < 5) {
            explosion = getOldParticle();
        }  else if (version[1] == 20 && version[2] >= 5) {
            explosion = Particle.EXPLOSION;
        } else if (version[1] >= 21) {
            explosion = Particle.EXPLOSION;
        } else {
            explosion = getOldParticle();
        }
    }

    public static Particle getExplosion() {
        return explosion;
    }

    private static Particle getOldParticle() {
        try {
            return Particle.valueOf("EXPLOSION_NORMAL");
        } catch (Exception e) {
            throw new RuntimeException("Version support not found");
        }
    }
}
