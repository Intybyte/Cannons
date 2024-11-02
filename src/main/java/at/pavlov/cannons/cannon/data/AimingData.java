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
    private double aimingPitch;
    private double aimingYaw;
    // is the cannon aiming at the given direction
    private boolean aimingFinished;
    // time it was last aimed
    private long lastAimed;
}
