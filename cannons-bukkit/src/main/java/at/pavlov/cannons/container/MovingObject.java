package at.pavlov.cannons.container;

import at.pavlov.internal.key.Key;
import com.cryptomorin.xseries.XEntityType;
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
    private final Key entityType;

    public MovingObject(Location loc, Vector vel, Key entityType) {
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

        // 1. Move based on current velocity
        this.loc.add(this.vel);

        // 2. Apply drag to velocity
        this.vel.multiply(drag);

        // 3. Apply gravity
        this.vel.subtract(new Vector(0, gravity, 0));

        // 4. Apply constant acceleration if present
        Double accelerationPower = getConstantAccelerationPower();
        if (accelerationPower != null) {
            Vector acceleration = vel.clone().normalize().multiply(accelerationPower);
            this.vel.add(acceleration);
        }
    }

    /**
     * Returns the constant acceleration power for projectiles that use it (e.g., fireballs).
     * Returns null if the entity does not use constant acceleration.
     *
     * @return the acceleration power (e.g., 0.1) or null if not applicable
     */
    public Double getConstantAccelerationPower() {
        return switch (entityType.full()) {
            case "minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball",
                 "minecraft:wither_skull", "minecraft:shulker_bullet" -> 0.1; // vanilla default
            default -> null;
        };
    }

    /**
     * Returns a normalized positive value of the gravity, used with Vector#subtract,
     * higher values mean the projectile falls faster (duh)
     *
     * @return gravity to subtract to a specific entity type matching vanilla implementation
     */
    public double getGravity() {
        final EntityType breezeWindCharge = XEntityType.BREEZE_WIND_CHARGE.get();
        if (breezeWindCharge != null && entityType.matches(breezeWindCharge.getKey().toString())) {
            return 0.0;
        }

        return switch (entityType.full()) {
            case "minecraft:arrow", "minecraft:trident", "minecraft:spectral_arrow" -> 0.05;
            case "minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball",
                 "minecraft:wither_skull", "minecraft:shulker_bullet" -> 0.0;
            default -> 0.03;
        };
    }

    /**
     * Returns a value between 0 and 1.0, a small float means that the projectile
     * will get slowed down, whereas a high number will not change much the original vector,
     * used with Vector#multiply.
     * <br>
     * Drag force is a mechanical force that opposes the motion of an object moving through a fluid.
     *
     * @param inWater the projectile is in water, in this case the drag is stronger
     *
     * @return drag multiplier for a specific entity matching vanilla implementation
     */
    // they got some real PhD Physicists at mojang so instead of calling it drag
    // in their code it is called 'getInertia' even if it handled like its opposite.
    // in case someone else has to work on this and a new projectile entity needs to be added
    // now you know where to go on paper's internal code.
    public double getDrag(boolean inWater) {
        final EntityType breezeWindCharge = XEntityType.BREEZE_WIND_CHARGE.get();
        if (breezeWindCharge != null && entityType.matches(breezeWindCharge.getKey().toString())) {
            return 1.0;
        }

        return switch (entityType.full()) {
            case "minecraft:arrow", "minecraft:spectral_arrow"  -> inWater ? 0.6 : 0.99;
            case "minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball",
                 "minecraft:wither_skull", "minecraft:shulker_bullet" -> 0.95;
            case "minecraft:trident" -> 0.99;
            default -> inWater ? 0.8 : 0.99; // Water Drag - Air Drag
        };
    }

    /**
     * Reverts an update of the projectile position.
     *
     * @param inWater true if the projectile is in water
     */
    public void revertProjectileLocation(boolean inWater) {
        double drag = getDrag(inWater);
        double gravity = getGravity();

        // 1. Revert constant acceleration if present
        Double accelerationPower = getConstantAccelerationPower();
        if (accelerationPower != null) {
            Vector acceleration = vel.clone().normalize().multiply(accelerationPower);
            this.vel.subtract(acceleration);
        }

        // 2. Revert gravity
        this.vel.add(new Vector(0, gravity, 0));

        // 3. Revert drag
        this.vel.multiply(1.0 / drag);

        // 4. Revert movement
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

