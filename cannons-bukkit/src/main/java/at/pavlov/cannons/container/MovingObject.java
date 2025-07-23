package at.pavlov.cannons.container;

import at.pavlov.internal.Key;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Data public class MovingObject {

    private UUID world;
    private Vector loc;
    private Vector vel;
    private final Key projectileKey;
    private final @NotNull ProjectilePhysics physics;

    public MovingObject(Location loc, Vector vel, Key projectileKey) {
        this.world = loc.getWorld().getUID();
        this.loc = loc.toVector();
        this.vel = vel;
        this.projectileKey = projectileKey;
        this.physics = Registries.PROJECTILE_PHYSICS.of(projectileKey, ProjectilePhysics.DEFAULT);
    }

    /**
     * calculates the new position for the projectile
     *
     * @param inWater the projectile is in water
     */
    public void updateProjectileLocation(boolean inWater) {
        double drag = this.physics.getDrag(inWater);
        double gravity = this.physics.getGravity();

        // 1. Move based on current velocity
        this.loc.add(this.vel);

        // 2. Apply drag to velocity
        this.vel.multiply(drag);

        // 3. Apply gravity
        this.vel.subtract(new Vector(0, gravity, 0));

        // 4. Apply constant acceleration if present
        Double accelerationPower = this.physics.getConstantAcceleration();
        if (accelerationPower != null) {
            Vector acceleration = vel.clone().normalize().multiply(accelerationPower);
            this.vel.add(acceleration);
        }
    }

    /**
     * Reverts an update of the projectile position.
     *
     * @param inWater true if the projectile is in water
     */
    public void revertProjectileLocation(boolean inWater) {
        double drag = this.physics.getDrag(inWater);
        double gravity = this.physics.getGravity();

        // 1. Revert constant acceleration if present
        Double accelerationPower = this.physics.getConstantAcceleration();
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

