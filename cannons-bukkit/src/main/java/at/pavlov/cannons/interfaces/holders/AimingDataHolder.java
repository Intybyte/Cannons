package at.pavlov.cannons.interfaces.holders;

import at.pavlov.internal.cannon.data.AimingData;
import at.pavlov.internal.cannon.functionalities.Updatable;

public interface AimingDataHolder extends Updatable {
    AimingData getAimingData();
    void setAimingData(AimingData aimingData);

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
