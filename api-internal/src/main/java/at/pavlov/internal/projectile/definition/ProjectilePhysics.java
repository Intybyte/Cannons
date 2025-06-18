package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.key.EntityKeyHolder;
import at.pavlov.internal.key.Key;
import at.pavlov.internal.key.KeyHolder;

public interface ProjectilePhysics extends KeyHolder, EntityKeyHolder {
    ProjectilePhysics DEFAULT = new ProjectilePhysics() {
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
        public Key getEntityKey() {
            return null;
        }

        @Override
        public Key getKey() {
            return null;
        }
    };

    Double getConstantAcceleration();
    double getGravity();
    double getDrag();
    double getWaterDrag();
}
