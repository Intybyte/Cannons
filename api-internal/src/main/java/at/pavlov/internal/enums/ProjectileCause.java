package at.pavlov.internal.enums;


import lombok.Getter;

@Getter
public enum ProjectileCause {
    //Error Messages
    PLAYER_FIRED("Player fired"),
    REDSTONE_FIRED("Redstone fired"),
    SENTRY_FIRED("Sentry fired"),
    SPAWNED_PROJECTILE("Spawned projectile"),
    DEFLECTED_PROJECTILE("Deflected projectile"),
    UNKNOWN_FIRED("fired unknown cause");

    private final String string;

    ProjectileCause(String str) {
        this.string = str;
    }
}
