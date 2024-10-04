package at.pavlov.cannons.container;

import at.pavlov.cannons.Enum.TargetType;
import at.pavlov.cannons.cannon.Cannon;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Target {

    private final String name;
    private final TargetType targetType;
    private final EntityType type;
    private final UUID uniqueId;
    private final Location groundLocation;
    private final Location centerLocation;
    private final Vector velocity;

    public Target(String name, TargetType targetType, EntityType type, UUID uid, Location groundLocation, Location centerLocation, Vector velocity) {
        this.name = name;
        this.targetType = targetType;
        this.type = type;
        this.uniqueId = uid;
        this.groundLocation = groundLocation;
        this.centerLocation = centerLocation;
        this.velocity = velocity;
    }

    public Target(Entity entity) {
        this.name = entity.getName();
        this.targetType = switch (entity) {
            case Player player -> TargetType.PLAYER;
            case Monster monster -> TargetType.MONSTER;
            case Animals animals -> TargetType.ANIMAL;
            default -> TargetType.OTHER;
        };
        this.type = entity.getType();
        this.uniqueId = entity.getUniqueId();
        // aim for center of mass
        if (entity instanceof LivingEntity)
            this.centerLocation = ((LivingEntity) entity).getEyeLocation().clone().add(0., -0.5, 0.);
        else
            this.centerLocation = entity.getLocation().clone().add(0., 0.5, 0.);
        this.groundLocation = entity.getLocation().clone();
        this.velocity = entity.getVelocity();
    }
    public Target(Cannon cannon) {
        this.name = cannon.getCannonName();
        this.targetType = TargetType.CANNON;
        this.type = null;
        this.uniqueId = cannon.getUID();
        this.groundLocation = cannon.getRandomBarrelBlock().clone().add(0.5, 0.0, 0.5);
        this.centerLocation = cannon.getRandomBarrelBlock().clone().add(0.5, 0.5, 0.5);
        this.velocity = cannon.getVelocity();
    }

    public String toString() {
        return "name: " + this.name + "UID: " + this.uniqueId + "location: " + this.centerLocation.toString() + " velocity: " + velocity.toString();
    }

    public String getName() {
        return name;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public Location getGroundLocation() {
        return groundLocation;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public EntityType getType() {
        return type;
    }

    public TargetType getTargetType() {
        return targetType;
    }


}
