package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.data.AimingData;
import at.pavlov.cannons.interfaces.functionalities.Updatable;

public interface AimingDataHolder extends Updatable {
    AimingData getAimingData();
    void setAimingData(AimingData aimingData);

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

    default long getLastAimed() {
        return getAimingData().getLastAimed();
    }

    default void setLastAimed(long lastAimed) {
        getAimingData().setLastAimed(lastAimed);
    }

    default long getTimestampAimingMode() {
        return getAimingData().getTimestampAimingMode();
    }

    default void setTimestampAimingMode(long timestampAimingMode) {
        getAimingData().setTimestampAimingMode(timestampAimingMode);
    }
}
