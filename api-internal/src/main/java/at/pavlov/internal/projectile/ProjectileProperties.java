package at.pavlov.internal.projectile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectileProperties {
    SUPERBREAKER("SUPERBREAKER"),
    INCENDIARY("INCENDIARY"),
    SHOOTER_AS_PASSENGER("SHOOTER_AS_PASSENGER"),
    HUMAN_CANNONBALL("HUMAN_CANNONBALL"),
    TELEPORT("TELEPORT"),
    OBSERVER("OBSERVER");

    private final String name;

    public static ProjectileProperties getByName(String str) {
        if (str == null) {
            return null;
        }

        for (ProjectileProperties p : ProjectileProperties.values()) {
            if (str.equalsIgnoreCase(p.name)) {
                return p;
            }
        }
        return null;
    }
}
