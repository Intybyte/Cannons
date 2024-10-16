package at.pavlov.cannons.cannon.data;

import at.pavlov.cannons.projectile.Projectile;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data public class FiringData {
    private int loadedGunpowder;
    private Projectile loadedProjectile;
    private double soot;
    private int projectilePushed;
    // amount of fired cannonballs with this cannon
    private long firedCannonballs;
    // the projectile which was loaded previously
    private Projectile lastFiredProjectile;
    private int lastFiredGunpowder;
    // time the cannon was last time fired
    private long lastFired;
    // it was loaded for the last time
    private long lastLoaded;

    public void setSoot(double soot) {
        this.soot = Math.max(soot, 0);
    }

    public void setProjectilePushed(int pushed) {
        this.projectilePushed = Math.max(pushed, 0);
    }
}