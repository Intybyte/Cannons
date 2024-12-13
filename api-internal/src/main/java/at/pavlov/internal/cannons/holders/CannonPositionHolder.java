package at.pavlov.internal.cannons.holders;

import at.pavlov.internal.cannons.data.CannonPosition;
import at.pavlov.internal.cannons.functionalities.Updatable;
import at.pavlov.internal.container.location.CannonVector;

import java.util.UUID;

public interface CannonPositionHolder<Direction> extends Updatable {

    CannonPosition<Direction> getCannonPosition();
    void setCannonPosition(CannonPosition<Direction> position);

    default CannonVector getVelocity() {
        return getCannonPosition().getVelocity();
    }

    default void setVelocity(CannonVector velocity) {
        getCannonPosition().setVelocity(velocity);
    }

    default boolean isOnShip() {
        return getCannonPosition().isOnShip();
    }

    default void setOnShip(boolean onShip) {
        getCannonPosition().setOnShip(onShip);
        this.hasUpdated();
    }

    default CannonVector getOffset() {
        return getCannonPosition().getOffset();
    }

    default void setOffset(CannonVector offset) {
        getCannonPosition().setOffset(offset);
        this.hasUpdated();
    }

    default Direction getCannonDirection() {
        return getCannonPosition().getCannonDirection();
    }

    default void setCannonDirection(Direction cannonDirection) {
        getCannonPosition().setCannonDirection(cannonDirection);
        this.hasUpdated();
    }

    default UUID getWorld() {
        return getCannonPosition().getWorld();
    }

    default void setWorld(UUID world) {
        getCannonPosition().setWorld(world);
        this.hasUpdated();
    }

    /**
     * updates the location of the cannon
     *
     * @param moved - how far the cannon has been moved
     */
    void move(CannonVector moved);
}
