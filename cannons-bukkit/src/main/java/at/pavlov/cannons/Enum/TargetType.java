package at.pavlov.cannons.Enum;

import at.pavlov.internal.cannon.holders.SentryDataHolder;

public enum TargetType {
    MONSTER,
    ANIMAL,
    PLAYER,
    OTHER,
    CANNON,
    AIRSHIP,
    BOAT,
    SUBMARINE;

    public boolean isAllowed(SentryDataHolder cannon) {
        return switch (this) {
            case MONSTER -> cannon.isTargetMob();
            case PLAYER -> cannon.isTargetPlayer();
            case OTHER -> cannon.isTargetOther();
            case CANNON -> cannon.isTargetCannon();
            default -> false;
        };
    }
}
