package at.pavlov.cannons.aim;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.utils.CannonsUtil;
import lombok.Data;

@Data
public class GunAngles {
    private double horizontal;
    private double vertical;

    public GunAngles(double horizontal, double vertical) {
        this.setHorizontal(horizontal);
        this.setVertical(vertical);
    }

    public double getAbsHorizontal() { return Math.abs(horizontal); }

    public double getAbsVertical() { return Math.abs(vertical); }

    /**
     * evaluates the difference between actual cannon direction and the given direction
     *
     * @param cannon operated cannon
     * @param yaw    yaw of the direction to aim
     * @param pitch  pitch of the direction to aim
     * @return new cannon aiming direction
     */
    public static GunAngles getGunAngle(Cannon cannon, double yaw, double pitch) {
        double horizontal = yaw - CannonsUtil.directionToYaw(cannon.getCannonDirection()) - cannon.getTotalHorizontalAngle();
        horizontal = horizontal % 360;
        while (horizontal < -180)
            horizontal = horizontal + 360;

        return new GunAngles(horizontal, -pitch - cannon.getTotalVerticalAngle());
    }
}