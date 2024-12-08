package at.pavlov.internal.cannons.data;

import lombok.Data;

@Data
public class AmmoLoadingData<Projectile> {

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
