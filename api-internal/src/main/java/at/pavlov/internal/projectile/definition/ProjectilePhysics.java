package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.key.EntityKeyHolder;
import at.pavlov.internal.Key;
import at.pavlov.internal.key.KeyHolder;

import java.util.Objects;

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

    /**
     * Returns the constant acceleration power for projectiles that use it (e.g., fireballs).
     * Returns null if the entity does not use constant acceleration.
     *
     * @return the acceleration power (e.g., 0.1) or null if not applicable
     */
    Double getConstantAcceleration();

    /**
     * Returns a normalized positive value of the gravity, used with Vector#subtract,
     * higher values mean the projectile falls faster (duh)
     *
     * @return gravity to subtract to a specific entity type matching vanilla implementation
     */
    double getGravity();


    // they got some real PhD Physicists at mojang so instead of calling it drag
    // in their code it is called 'getInertia' even if it handled like its opposite.
    // in case someone else has to work on this and a new projectile entity needs to be added
    // now you know where to go on paper's internal code.

    /**
     * Returns a value between 0 and 1.0, a small float means that the projectile
     * will get slowed down, whereas a high number will not change much the original vector,
     * used with Vector#multiply.
     * <br>
     * Drag force is a mechanical force that opposes the motion of an object moving through a fluid.
     *
     *
     * @return drag multiplier for a specific entity matching vanilla implementation
     */
    double getDrag();

    /**
     * Returns a value between 0 and 1.0, a small float means that the projectile
     * will get slowed down, whereas a high number will not change much the original vector,
     * used with Vector#multiply.
     * <br>
     * Drag force is a mechanical force that opposes the motion of an object moving through a fluid.
     *
     *
     * @return drag multiplier for a specific entity matching vanilla implementation
     */
    double getWaterDrag();

    default double getDrag(boolean inWater) {
        return inWater ? this.getWaterDrag() : this.getDrag();
    }

    default boolean matches(ProjectilePhysics other) {
        if (other == null) return false;

        return Objects.equals(this.getConstantAcceleration(), other.getConstantAcceleration()) &&
                Double.compare(this.getGravity(), other.getGravity()) == 0 &&
                Double.compare(this.getDrag(), other.getDrag()) == 0 &&
                Double.compare(this.getWaterDrag(), other.getWaterDrag()) == 0;
    }
}
