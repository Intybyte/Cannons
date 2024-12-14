package at.pavlov.cannons.container;

import at.pavlov.bukkit.projectile.BukkitProjectile;

import java.util.UUID;

public record DeathCause(BukkitProjectile projectile, UUID cannonUID, UUID shooterUID) {

}
