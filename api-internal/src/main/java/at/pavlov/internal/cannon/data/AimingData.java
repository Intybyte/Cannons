package at.pavlov.internal.cannon.data;

import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data public class AimingData {
    // sentry aiming angles the cannon tries to reach
    private double aimingPitch = 0.0;
    private double aimingYaw = 0.0;
    // is the cannon aiming at the given direction
    private boolean aimingFinished = false;
    // time it was last aimed
    private long lastAimed;
    // time stamp of the player last time inside the aiming mode range (needs a certain time to disable aiming mode)
    private long timestampAimingMode;
}
