package at.pavlov.bukkit.container;

import at.pavlov.bukkit.projectile.BukkitProjectile;
import at.pavlov.internal.container.DeathCause;

import java.util.UUID;

public final class BukkitDeathCause extends DeathCause<BukkitProjectile> {
    public BukkitDeathCause(BukkitProjectile projectile, UUID cannonUID, UUID shooterUID) {
        super(projectile, cannonUID, shooterUID);
    }
}
