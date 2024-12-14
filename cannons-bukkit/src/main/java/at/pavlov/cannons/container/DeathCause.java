package at.pavlov.cannons.container;

import at.pavlov.bukkit.projectile.Projectile;

import java.util.UUID;

public record DeathCause(Projectile projectile, UUID cannonUID, UUID shooterUID) {

}
