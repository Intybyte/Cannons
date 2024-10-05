package at.pavlov.cannons.interfaces;

import at.pavlov.cannons.interfaces.holders.FiringDataHolder;
import at.pavlov.cannons.projectile.Projectile;

import java.util.UUID;

public interface ICannon extends ITurret, FiringDataHolder {

    boolean sameType(ICannon cannon);

    void setUID(UUID id);
    UUID getUID();

    //region Firing Data Handling
    //region Soot
    default void setSoot(double soot) {
        getFiringData().setSoot(soot);
        this.hasUpdated();
    }
    default double getSoot() {
        return getFiringData().getSoot();
    }

    default boolean isClean() {
        return getSoot() < 1;
    }

    /**
     * reduces the soot of the cannon by the given amount
     *
     * @param amount soot to reduce
     */
    default void cleanCannon(int amount) {
        setSoot(getSoot() - amount);
    }
    //endregion

    default void setLoadedGunpowder(int gunpowder) {
        getFiringData().setLoadedGunpowder(gunpowder);
        this.hasUpdated();
    }
    default int getLoadedGunpowder() {
        return getFiringData().getLoadedGunpowder();
    }

    default void setLoadedProjectile(Projectile projectile) {
        getFiringData().setLoadedProjectile(projectile);
        this.hasUpdated();
    }
    default Projectile getLoadedProjectile() {
        return getFiringData().getLoadedProjectile();
    }

    default Projectile getLastFiredProjectile() {
        return getFiringData().getLastFiredProjectile();
    }
    default int getLastFiredGunpowder() {
        return getFiringData().getLastFiredGunpowder();
    }

    //region Pushed Projectile
    default void setProjectilePushed(int pushed) {
        getFiringData().setProjectilePushed(pushed);
        this.hasUpdated();
    }
    default int getProjectilePushed() {
        return getFiringData().getProjectilePushed();
    }

    /**
     * is the Projectile in place and done
     *
     * @return if the projectile is ready to fire
     */
    default boolean isProjectilePushed() {
        return (getProjectilePushed() == 0);
    }

    /**
     * pushes the projectile to the gunpowder
     *
     * @param amount how often the projectile is pushed
     */
    default void pushProjectile(int amount) {
        setProjectilePushed(getProjectilePushed() - amount);
    }
    //endregion
    //endregion

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

    void hasUpdated();
    boolean isUpdated();
}
