package at.pavlov.cannons.cannon.data;

import at.pavlov.cannons.projectile.Projectile;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data
public class AmmoLoadingData {

    private int loadedGunpowder;
    private Projectile loadedProjectile;
    private double soot;
    private int projectilePushed;

    // cannon temperature
    private double tempValue = 0.0;
    private long tempTimestamp = 0;


    public void setSoot(double soot) {
        this.soot = Math.max(soot, 0);
    }

    public void setProjectilePushed(int pushed) {
        this.projectilePushed = Math.max(pushed, 0);
    }
}
