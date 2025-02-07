package at.pavlov.cannons.container;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.UUID;

@Data public class MovingObject {

    //location and speed
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

    public double getGravity() {
        return switch (entityType) {
            case ARROW -> 0.05000000074505806;
            case FIREBALL, SMALL_FIREBALL, DRAGON_FIREBALL,
                 WITHER_SKULL,
                 SHULKER_BULLET -> 0.0;
            default -> 0.03;
        };
    }

    public double getDrag(boolean inWater) {
        return switch (entityType) {
            case ARROW -> {
                if (inWater) {
                    yield 0.6F;
                }

                yield 0.99F;
            }
            case FIREBALL, SMALL_FIREBALL, DRAGON_FIREBALL ,
                 WITHER_SKULL,
                 SHULKER_BULLET -> 0.95F;
            default -> inWater ? 0.8F : 0.99F; // Air Drag - Water Drag
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

