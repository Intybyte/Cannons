package at.pavlov.internal.enums;

import lombok.Getter;

/**
 * Created by Peter on 25.04.2014.
 */
@Getter
public enum FakeBlockType {
    AIMING("Aiming"),
    EXPLOSION("Explosion"),
    MUZZLE_FIRE("Muzzle fire"),
    IMPACT_PREDICTOR("Impact predictor"),
    WATER_SPLASH("Water splash"),
    SMOKE_TRAIL("Smoke trail");

    private final String str;

    FakeBlockType(String type) {
        this.str = type;
    }

}
