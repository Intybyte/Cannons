package at.pavlov.cannons.cannon.data;

import at.pavlov.cannons.projectile.Projectile;
import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.UUID;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data public class FiringData {
    // amount of fired cannonballs with this cannon
    private long firedCannonballs;
    // the projectile which was loaded previously
    private Projectile lastFiredProjectile;
    private int lastFiredGunpowder;
    // time the cannon was last time fired
    private long lastFired;
    // it was loaded for the last time
    private long lastLoaded;
    // the player which has used the cannon last, important for firing with redstone button
    private UUID lastUser;
    // time point of the last start of the firing sequence (used in combination with isFiring)
    private long lastIgnited;
}