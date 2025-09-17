package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.Key;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeyedDefaultProjectile implements ProjectilePhysics {
    private final Key entityKey;

    @Override
    public Double getConstantAcceleration() {
        return null;
    }

    @Override
    public double getGravity() {
        return 0.03;
    }

    @Override
    public double getDrag() {
        return 0.99;
    }

    @Override
    public double getWaterDrag() {
        return 0.8;
    }

    @Override
    public Key getKey() {
        return entityKey;
    }
}
