package at.pavlov.internal.enums;


import lombok.Getter;

@Getter
public enum ProjectileCause {
    //Error Messages
    PlayerFired("Player fired"),
    RedstoneFired("Redstone fired"),
    SentryFired("Sentry fired"),
    SpawnedProjectile("Spawned projectile"),
    DeflectedProjectile("Deflected projectile"),
    UnknownFired("fired unknown cause");

    private final String string;

    ProjectileCause(String str) {
        this.string = str;
    }
}
