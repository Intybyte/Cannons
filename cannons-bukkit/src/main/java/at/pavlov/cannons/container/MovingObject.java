package at.pavlov.cannons.container;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.UUID;

@Data public class MovingObject {

    private UUID world;
    private Vector loc;
    private Vector vel;
    private final EntityType entityType;

    public MovingObject(Location loc, Vector vel, EntityType entityType) {
        world = loc.getWorld().getUID();
        this.loc = loc.toVector();
        this.vel = vel;
        this.entityType = entityType;
    }

    /**
     * calculates the new position for the projectile
     *
     * @param inWater the projectile is in water
     */
    public void updateProjectileLocation(boolean inWater) {
        double drag = getDrag(inWater);
        double gravity = getGravity();
        //update location
        this.loc.add(this.vel);
        //slow down projectile
        this.vel.multiply(drag);
        //apply gravity
        this.vel.subtract(new Vector(0, gravity, 0));
    }

    /**
     * Returns a normalized positive value of the gravity, used with Vector#subtract,
     * higher values mean the projectile falls faster (duh)
     *
     * @return gravity to subtract to a specific entity type matching vanilla implementation
     */
    public double getGravity() {
        return switch (entityType) {
            case ARROW -> 0.05000000074505806;
            case FIREBALL, SMALL_FIREBALL, DRAGON_FIREBALL,
                 WITHER_SKULL,
                 SHULKER_BULLET -> 0.0;
            default -> 0.03;
        };
    }

    /**
     * Returns a value between 0 and 0.99, the smaller the number the stronger the drag
     * which means the projectile will get slowed down, used with Vector#multiply
     *
     * @param inWater the projectile is in water, in this case the drag is stronger
     *
     * @return drag multiplier for a specific entity matching vanilla implementation
     */
    public double getDrag(boolean inWater) {
        return switch (entityType) {
            case ARROW -> inWater ? 0.6 : 0.99;
            case FIREBALL, SMALL_FIREBALL, DRAGON_FIREBALL,
                 WITHER_SKULL,
                 SHULKER_BULLET -> 0.95;
            default -> inWater ? 0.8 : 0.99; // Water Drag - Air Drag
        };
    }

    /**
     * reverts and update of the projectile position
     *
     * @param inWater the projectile is in water
     */
    public void revertProjectileLocation(boolean inWater) {
        double drag = getDrag(inWater);
        double gravity = getGravity();
        //apply gravity
        this.vel.add(new Vector(0, gravity, 0));
        //slow down projectile
        this.vel.multiply(1.0 / drag);
        //update location
        this.loc.subtract(this.vel);
    }

    /**
     * teleports the projectile to this location
     *
     * @param loc the projectile will be teleported to this location
     * @param vel velocity of the object
     */
    public void teleport(Location loc, Vector vel) {
        this.loc = loc.toVector();
        this.vel = vel;
        this.world = loc.getWorld().getUID();
    }

    /**
     * returns the calculated location of the projectile
     *
     * @return the location where the projectile should be
     */
    public Location getLocation() {
        return loc.toLocation(Bukkit.getWorld(world));
    }

    public void setLocation(Location loc) {
        this.loc = loc.toVector();
        this.world = loc.getWorld().getUID();
    }
}

