package at.pavlov.internal.cannons.data;

import lombok.Data;

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
