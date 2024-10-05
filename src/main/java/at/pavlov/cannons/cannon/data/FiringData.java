package at.pavlov.cannons.cannon.data;

import at.pavlov.cannons.projectile.Projectile;
import lombok.Data;

@Data public class FiringData {
    private int loadedGunpowder;
    private Projectile loadedProjectile;
    private double soot;
    private int projectilePushed;
    // the projectile which was loaded previously
    private Projectile lastFiredProjectile;
    private int lastFiredGunpowder;

    public void setSoot(double soot) {
        this.soot = Math.max(soot, 0);
    }

    public void setProjectilePushed(int pushed) {
        this.projectilePushed = Math.max(pushed, 0);
    }
}