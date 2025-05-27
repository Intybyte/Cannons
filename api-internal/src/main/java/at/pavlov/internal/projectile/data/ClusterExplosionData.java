package at.pavlov.internal.projectile.data;

import lombok.Data;

@Data
public class ClusterExplosionData {
    private boolean clusterExplosionsEnabled;
    private boolean clusterExplosionsInBlocks;
    private int clusterExplosionsAmount;
    private double clusterExplosionsMinDelay;
    private double clusterExplosionsMaxDelay;
    private double clusterExplosionsRadius;
    private double clusterExplosionsPower;

    public double getRandomDelay() {
        return clusterExplosionsMinDelay + Math.random() * (clusterExplosionsMaxDelay - clusterExplosionsMinDelay);
    }
}
