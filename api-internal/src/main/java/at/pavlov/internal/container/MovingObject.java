package at.pavlov.internal.container;

import at.pavlov.internal.container.location.CannonVector;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data public abstract class MovingObject<Entity> {

    //location and speed
    protected UUID world;
    protected CannonVector loc;
    protected CannonVector vel;
    protected Entity entityType;

    /**
     * calculates the new position for the projectile
     * @param inWater the projectile is in water
     */
    public void updateProjectileLocation(boolean inWater) {
        double multiplier = getDragMultiplier(inWater);
        double gravity = getGravity();
        //update location
        this.loc.add(this.vel);
        //slow down projectile
        this.vel.multiply(multiplier);
        //apply gravity
        this.vel.subtract(new CannonVector(0, gravity, 0));
    }

    /**
     * Retrieves the drag multiplier based on the entity type and environment.
     *
     * @param inWater Whether the projectile is in water.
     * @return The drag multiplier.
     */
    protected double getDragMultiplier(boolean inWater) {
        if (entityType.equals(arrow())) {
            return inWater ? 0.6F : 0.99F;
        }

        if (fireballs().contains(entityType)) {
            return 0.95F;
        }

        return inWater ? 0.8F : 0.99F;
    }

    /**
     * Retrieves the gravity value based on the entity type and environment.
     *
     * @return The gravity value.
     */
    protected double getGravity() {
        if (entityType.equals(arrow())) {
            return 0.05000000074505806D;
        }
        if (fireballs().contains(entityType)) {
            return 0.0;
        }
        return 0.03F;
    }

    /**
     * reverts and update of the projectile position
     * @param inWater the projectile is in water
     */
    public void revertProjectileLocation(boolean inWater) {
        double multiplier = getDragMultiplier(inWater);
        double gravity = getGravity();
        //apply gravity
        this.vel.add(new CannonVector(0, gravity, 0));
        //slow down projectile
        this.vel.multiply(1.0 / multiplier);
        //update location
        this.loc.subtract(this.vel);
    }

    public abstract Entity arrow();
    public abstract List<Entity> fireballs();
}

