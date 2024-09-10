package at.pavlov.cannons.interfaces;

import at.pavlov.cannons.projectile.Projectile;

import java.util.UUID;

public interface ICannon extends ITurret {

    boolean sameType(ICannon cannon);

    void setUID(UUID id);
    UUID getUID();

    void setSoot(double soot);
    double getSoot();

    void setLoadedGunpowder(int gunpowder);
    int getLoadedGunpowder();

    void setLoadedProjectile(Projectile projectile);
    Projectile getLoadedProjectile();

    void setProjectilePushed(int pushed);
    int getProjectilePushed();

    void setHorizontalAngle(double angle);
    double getHorizontalAngle();

    void setVerticalAngle(double angle);
    double getVerticalAngle();

    void setTemperature(double temp);
    double getTemperature();

    void setTemperatureTimeStamp(long timeStamp);
    long getTemperatureTimeStamp();

    void setFiredCannonballs(long firedCannonballs);
    long getFiredCannonballs();

    void setPaid(boolean paid);
    boolean isPaid();
}
