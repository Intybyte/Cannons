package at.pavlov.bukkit.utils;

import at.pavlov.bukkit.builders.ParticleBuilder;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

public class FileReadUtils {

    public static ParticleBuilder readParticleBuilder(FileConfiguration cfg, String key) {
        var k = key + ".";

        Particle particle = Particle.valueOf(cfg.getString(k + "type", "CAMPFIRE_SIGNAL_SMOKE"));
        int count = cfg.getInt(k + "count", 1);
        double x = cfg.getDouble(k + "x_offset", 0.0);
        double y = cfg.getDouble(k + "y_offset", 0.0);
        double z = cfg.getDouble(k + "z_offset", 0.0);
        double speed = cfg.getDouble(k + "speed", 0.0);

        return new ParticleBuilder(particle, count, x, y, z, speed);
    }
}
