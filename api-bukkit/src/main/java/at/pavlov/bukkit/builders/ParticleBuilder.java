package at.pavlov.bukkit.builders;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Particle;

@AllArgsConstructor
@Data public class ParticleBuilder {
    private Particle particle;
    private int count;
    private double x, y, z;
    private double speed;

    public void at(Location l) {
        l.getWorld().spawnParticle(particle, l, count, x , y, z, speed, null, true);
    }
}
