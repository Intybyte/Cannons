package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.data.CannonPosition;
import at.pavlov.cannons.interfaces.Updatable;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.UUID;

public interface CannonPositionHolder extends Updatable {

    CannonPosition getCannonPosition();
    void setCannonPosition(CannonPosition position);

    default Vector getVelocity() {
        return getCannonPosition().getVelocity();
    }

    default void setVelocity(Vector velocity) {
        getCannonPosition().setVelocity(velocity);
    }

    default boolean isOnShip() {
        return getCannonPosition().isOnShip();
    }

    default void setOnShip(boolean onShip) {
        getCannonPosition().setOnShip(onShip);
        this.hasUpdated();
    }

    default Vector getOffset() {
        return getCannonPosition().getOffset();
    }

    default void setOffset(Vector offset) {
        getCannonPosition().setOffset(offset);
        this.hasUpdated();
    }

    default BlockFace getCannonDirection() {
        return getCannonPosition().getCannonDirection();
    }

    default void setCannonDirection(BlockFace cannonDirection) {
        getCannonPosition().setCannonDirection(cannonDirection);
        this.hasUpdated();
    }

    default UUID getWorld() {
        return getCannonPosition().getWorld();
    }

    /**
     * @return bukkit world from saved UUID
     */
    default World getWorldBukkit() {
        if (this.getWorld() == null) {
            return null;
        }
        World bukkitWorld = Bukkit.getWorld(this.getWorld());
        if (bukkitWorld == null)
            Cannons.logger().info("Can't find world: " + getWorld());
        return Bukkit.getWorld(this.getWorld());
        // return new Location(bukkitWorld, )
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
    default void move(Vector moved) {
        getOffset().add(moved);
        this.hasUpdated();
    }
}
