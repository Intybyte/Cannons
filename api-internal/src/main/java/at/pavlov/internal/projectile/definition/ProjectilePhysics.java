package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.key.EntityKeyHolder;
import at.pavlov.internal.key.KeyHolder;

public interface ProjectilePhysics extends KeyHolder, EntityKeyHolder {
    Double getConstantAcceleration();
    double getGravity();
    double getDrag();
    double getWaterDrag();
}
