package at.pavlov.internal.container;

import java.util.UUID;

public record DeathCause(String projectileID, UUID cannonUID, UUID shooterUID) {
}
