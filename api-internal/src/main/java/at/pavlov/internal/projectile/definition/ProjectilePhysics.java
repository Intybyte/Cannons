package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.registries.KeyHolder;

public interface ProjectilePhysics extends KeyHolder, EntityKeyHolder {
    Double getConstantAcceleration();
    double getGravity();
    double getDrag();
    double getWaterDrag();
}
