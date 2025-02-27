package at.pavlov.cannons.Enum;

import at.pavlov.cannons.cannon.Cannon;

public enum TargetType {
    MONSTER,
    ANIMAL,
    PLAYER,
    OTHER,
    CANNON,
    AIRSHIP,
    BOAT,
    SUBMARINE;

    public boolean isAllowed(Cannon cannon) {
        return switch (this) {
            case MONSTER -> cannon.isTargetMob();
            case PLAYER -> cannon.isTargetPlayer();
            case OTHER -> cannon.isTargetOther();
            case CANNON -> cannon.isTargetCannon();
            default -> false;
        };
    }
}
