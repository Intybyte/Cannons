package at.pavlov.cannons.projectile;

import lombok.Getter;

@Getter
public enum ProjectileProperties {
    SUPER_BREAKER("SUPER_BREAKER"),
    INCENDIARY("INCENDIARY"),
    SHOOTER_AS_PASSENGER("SHOOTER_AS_PASSENGER"),
    HUMAN_CANNONBALL("HUMAN_CANNONBALL"),
    TELEPORT("TELEPORT"),
    OBSERVER("OBSERVER");

    private final String name;

    ProjectileProperties(String str) {
        this.name = str;
    }

    public static ProjectileProperties getByName(String str) {
        if (str == null) {
            return null;
        }

        for (ProjectileProperties p : ProjectileProperties.values()) {
            if (str.equalsIgnoreCase(p.getName())) {
                return p;
            }
        }
        return null;
    }
}
