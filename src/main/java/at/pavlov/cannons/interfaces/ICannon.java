package at.pavlov.cannons.interfaces;

import at.pavlov.cannons.interfaces.holders.CannonDataHolder;
import at.pavlov.cannons.projectile.Projectile;

import java.util.UUID;

public interface ICannon extends ITurret, CannonDataHolder {

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

    //region Aiming Data Handling
    //region Vertical Angle
    default void setVerticalAngle(double verticalAngle) {
        var aimingData = getAimingData();
        aimingData.setVerticalAngle(verticalAngle);
        //the angle should not exceed the limits - if the cannon is on a ship, the max/min angles are smaller
        double maxVertical = getMaxVerticalAngle();
        if (aimingData.getVerticalAngle() > maxVertical)
            aimingData.setVerticalAngle(maxVertical);
        double minVertical = getMinVerticalAngle();
        if (aimingData.getVerticalAngle() < minVertical)
            aimingData.setVerticalAngle(minVertical);
        this.hasUpdated();
    }

    default double getVerticalAngle() {
        return getAimingData().getVerticalAngle();
    }

    double getMaxVerticalAngle();
    double getMinVerticalAngle();
    //endregion
    //region Horizontal Angle
    default double getHorizontalAngle() {
        return getAimingData().getHorizontalAngle();
    }

    default void setHorizontalAngle(double horizontalAngle) {
        var aimingData = getAimingData();
        aimingData.setHorizontalAngle(horizontalAngle);

        //the angle should not exceed the limits - if the cannon is on a ship, the max/min angles are smaller
        double maxHorizontal = getMaxHorizontalAngle();
        if (aimingData.getHorizontalAngle() > maxHorizontal)
            aimingData.setHorizontalAngle(maxHorizontal);
        double minHorizontal = getMinHorizontalAngle();
        if (aimingData.getHorizontalAngle() < minHorizontal)
            aimingData.setHorizontalAngle(minHorizontal);
        this.hasUpdated();
    }

    double getMaxHorizontalAngle();
    double getMinHorizontalAngle();
    //endregion
    //region Additional Angles
    default double getAdditionalHorizontalAngle() {
        return getAimingData().getAdditionalHorizontalAngle();
    }

    default void setAdditionalHorizontalAngle(double additionalHorizontalAngle) {
        getAimingData().setAdditionalHorizontalAngle(additionalHorizontalAngle);
    }

    default double getAdditionalVerticalAngle() {
        return getAimingData().getAdditionalVerticalAngle();
    }

    default void setAdditionalVerticalAngle(double additionalVerticalAngle) {
        getAimingData().setAdditionalVerticalAngle(additionalVerticalAngle);
    }
    //endregion
    //region Angle Calculations
    default double getTotalHorizontalAngle() {
        return this.getHorizontalAngle() + this.getAdditionalHorizontalAngle();
    }

    default double getTotalVerticalAngle() {
        return this.getVerticalAngle() + this.getAdditionalVerticalAngle();
    }
    //endregion

    //region Aiming Yaw & Pitch
    default double getAimingPitch() {
        return getAimingData().getAimingPitch();
    }

    default void setAimingPitch(double aimingPitch) {
        getAimingData().setAimingPitch(aimingPitch);
        this.hasUpdated();
    }

    default double getAimingYaw() {
        return getAimingData().getAimingYaw();
    }

    default void setAimingYaw(double aimingYaw) {
        getAimingData().setAimingYaw(aimingYaw);
        this.hasUpdated();
    }
    //endregion

    default boolean isAimingFinished() {
        return getAimingData().isAimingFinished();
    }

    default void setAimingFinished(boolean aimingFinished) {
        this.getAimingData().setAimingFinished(aimingFinished);
    }
    //endregion

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
