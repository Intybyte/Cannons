package at.pavlov.internal.cannon.holders;

import at.pavlov.internal.cannon.data.AngleData;
import at.pavlov.internal.cannon.functionalities.Updatable;

public interface AngleDataHolder extends Updatable {
    AngleData getAngleData();
    void setAngleData(AngleData angleData);

    //region Vertical Angle
    default void setVerticalAngle(double verticalAngle) {
        var angleData = getAngleData();
        angleData.setVerticalAngle(verticalAngle);
        //the angle should not exceed the limits - if the cannon is on a ship, the max/min angles are smaller
        double maxVertical = getMaxVerticalAngle();
        if (angleData.getVerticalAngle() > maxVertical)
            angleData.setVerticalAngle(maxVertical);
        double minVertical = getMinVerticalAngle();
        if (angleData.getVerticalAngle() < minVertical)
            angleData.setVerticalAngle(minVertical);
        this.hasUpdated();
    }

    default double getVerticalAngle() {
        return getAngleData().getVerticalAngle();
    }

    double getMaxVerticalAngle();
    double getMinVerticalAngle();
    //endregion
    //region Horizontal Angle
    default double getHorizontalAngle() {
        return getAngleData().getHorizontalAngle();
    }

    default void setHorizontalAngle(double horizontalAngle) {
        var angleData = getAngleData();
        angleData.setHorizontalAngle(horizontalAngle);

        //the angle should not exceed the limits - if the cannon is on a ship, the max/min angles are smaller
        double maxHorizontal = getMaxHorizontalAngle();
        if (angleData.getHorizontalAngle() > maxHorizontal)
            angleData.setHorizontalAngle(maxHorizontal);
        double minHorizontal = getMinHorizontalAngle();
        if (angleData.getHorizontalAngle() < minHorizontal)
            angleData.setHorizontalAngle(minHorizontal);
        this.hasUpdated();
    }

    double getMaxHorizontalAngle();
    double getMinHorizontalAngle();
    //endregion
    //region Additional Angles
    default double getAdditionalHorizontalAngle() {
        return getAngleData().getAdditionalHorizontalAngle();
    }

    default void setAdditionalHorizontalAngle(double additionalHorizontalAngle) {
        getAngleData().setAdditionalHorizontalAngle(additionalHorizontalAngle);
    }

    default double getAdditionalVerticalAngle() {
        return getAngleData().getAdditionalVerticalAngle();
    }

    default void setAdditionalVerticalAngle(double additionalVerticalAngle) {
        getAngleData().setAdditionalVerticalAngle(additionalVerticalAngle);
    }
    //endregion
    //region Angle Calculations
    default double getTotalHorizontalAngle() {
        return this.getHorizontalAngle() + this.getAdditionalHorizontalAngle();
    }

    double getTotalVerticalAngle();
    //endregion

    /**
     * get the default horizontal home position of the cannon
     *
     * @return default horizontal home position
     */
    double getHomeHorizontalAngle();

    /**
     * get the default vertical home position of the cannon
     *
     * @return default vertical home position
     */
    double getHomeVerticalAngle();

    /**
     * get the default horizontal home position of the cannon
     *
     * @return default horizontal home position
     */
    double getHomeYaw();

    /**
     * get the default vertical home position of the cannon
     *
     * @return default vertical home position
     */
    double getHomePitch();

    /**
     * if the cannon has the target in sight and angles are set correctly
     *
     * @return true if aiminig is finished
     */
    boolean targetInSight();

    /**
     * whenever the cannon can aim in this direction or not
     *
     * @param yaw horizontal angle
     * @return true if it can aim this direction
     */
    boolean canAimYaw(double yaw);

    /**
     * whenever the cannon can aim in this direction or not
     *
     * @param pitch vertical angle
     * @return true if it can aim this direction
     */
    boolean canAimPitch(double pitch);

    double verticalAngleToPitch(double vertical);

    default double getMaxVerticalPitch() {
        return verticalAngleToPitch(getMaxVerticalAngle());
    }

    default double getMinVerticalPitch() {
        return verticalAngleToPitch(getMinVerticalAngle());
    }

    default double getVerticalPitch() {
        return verticalAngleToPitch(getVerticalAngle());
    }

    double horizontalAngleToYaw(double horizontal);

    default double getMaxHorizontalYaw() {
        return horizontalAngleToYaw(getMaxHorizontalAngle());
    }

    default double getMinHorizontalYaw() {
        return horizontalAngleToYaw(getMinHorizontalAngle());
    }

    default double getHorizontalYaw() {
        return horizontalAngleToYaw(getHorizontalAngle());
    }
}
