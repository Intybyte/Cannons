package at.pavlov.internal.projectile;

import at.pavlov.internal.container.MovingObject;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import at.pavlov.internal.enums.ProjectileCause;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public abstract class FlyingProjectile <
        PlatformProjectile,
        Proj extends Projectile<?, ?, ?, ?, ?, ?, ?, ?, ?>,
        MO extends MovingObject<?>,
        Source
        > {
    @Getter
    protected long spawnTime;

    @Getter
    protected UUID entityUID;
    @Getter
    protected UUID shooterUID;
    @Getter
    protected UUID worldUID;
    @Setter
    @Getter
    protected UUID cannonUID;
    @Getter
    protected Proj projectile;
    @Getter
    protected Source source;
    //location of the shooterUID before firing - important for teleporting the player back - observer property
    @Getter
    protected Coordinate playerlocation;
    @Setter
    @Getter
    protected Coordinate impactLocation;
    //block which caused the cannonball explosion - can be null
    @Setter
    @Getter
    protected Coordinate impactBlock;
    @Setter
    @Getter
    protected Coordinate lastSmokeTrailLocation;
    //Important for visual splash effect when the cannonball hits the water surface
    @Getter
    protected boolean inWater;
    @Setter
    @Getter
    protected boolean wasInWater;
    //if the teleport was already performed
    @Setter
    @Getter
    protected boolean teleported;
    //was the projectile fired by a player, redstone or a sentry
    @Getter
    protected ProjectileCause projectileCause;
    @Setter
    @Getter
    protected boolean detonated;
    protected MO predictor;

    /**
     * Returns the entity of the flying projectile
     * This is time consuming, the projectile should be cached
     *
     *
     * @return
     */
    public abstract PlatformProjectile getProjectileEntity();

    /**
     * check if the projectile in in a liquid
     *
     * @return true if the projectile is in a liquid
     */
    protected abstract boolean isInWaterCheck(PlatformProjectile projectile_entity);

    /**
     * returns if the projectile has entered the water surface and updates also inWater
     *
     * @return true if the projectile has entered water
     */
    public boolean updateWaterSurfaceCheck(PlatformProjectile projectile_entity) {
        inWater = isInWaterCheck(projectile_entity);
        boolean isSurface = !wasInWater && inWater;
        wasInWater = inWater;
        return isSurface;
    }

    /**
     * searches for the projectile and checks if the projectile is still alive and valid
     *
     * @return returns false if the projectile entity is null
     */
    public boolean isValid() {
        return isValid(getProjectileEntity());
    }

    /**
     * if the projectile is still alive and valid
     * a projectile is valid if it has an entity, is not below -64 and younger than 1h (60*60*1000)
     *
     * @return returns false if the projectile entity is null
     */
    public abstract boolean isValid(PlatformProjectile projectile_entity);

    /**
     * updated the location and speed of the projectile to the expected values
     */
    public void update() {
        predictor.updateProjectileLocation(isInWater());
    }

    /**
     * revert update of the location
     */
    public void revertUpdate() {
        predictor.revertProjectileLocation(isInWater());
    }

    /**
     * returns the calculated location of the projectile
     *
     * @return the location where the projectile should be
     */
    public Coordinate getExpectedCoordinate() {
        return predictor.getCoordinate();
    }

    /**
     * teleports the projectile to the predicted location
     */
    public abstract void teleportToPrediction(PlatformProjectile projectile_entity);

    /**
     * teleports the projectile to the given location
     *
     * @param loc target location
     * @param vel velocity of the projectile
     */
    public abstract void teleport(Coordinate loc, CannonVector vel);

    @Override
    public int hashCode() {
        //compare projectile entities
        return entityUID.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        //equal if the projectile entities are equal
        if (!(obj instanceof FlyingProjectile fp)) {
            return false;
        }

        return this.getEntityUID().equals(fp.getEntityUID());
    }
}
