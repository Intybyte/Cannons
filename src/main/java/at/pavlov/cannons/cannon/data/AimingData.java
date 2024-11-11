package at.pavlov.cannons.cannon.data;

import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data public class AimingData {
    // angles
    private double horizontalAngle;
    private double verticalAngle;
    // additional angle if the cannon is mounted e.g. a ship which is facing a different angle
    private double additionalHorizontalAngle;
    private double additionalVerticalAngle;
    // sentry aiming angles the cannon tries to reach
    private double aimingPitch = 0.0;
    private double aimingYaw = 0.0;
    // is the cannon aiming at the given direction
    private boolean aimingFinished = false;
    // time it was last aimed
    private long lastAimed;
}
