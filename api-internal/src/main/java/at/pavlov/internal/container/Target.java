package at.pavlov.internal.container;

import at.pavlov.internal.cannons.holders.CannonMainDataHolder;
import at.pavlov.internal.cannons.holders.CannonPositionHolder;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import at.pavlov.internal.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.UUID;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public abstract class Target<Entity> {
    protected final String name;
    protected final TargetType targetType;
    protected final Entity type;
    protected final UUID uniqueId;
    protected final Coordinate groundLocation;
    protected final Coordinate centerLocation;
    protected final CannonVector velocity;

    /*
    public Target(Entity entity) {
        this(
                entity.getName(),
                entityTrasformer(entity),
                entity.getType(),
                entity.getUniqueId(),
                entity.getLocation().clone(),
                entity instanceof LivingEntity livingEntity ?
                        livingEntity.getEyeLocation().clone().add(0.0, -0.5, 0.0) :
                        entity.getLocation().clone().add(0.0, 0.5, 0.0),
                entity.getVelocity()
        );
    }*/

    public <T extends CannonMainDataHolder & CannonPositionHolder<?>> Target(T cannon) {
        this(
                cannon.getCannonName(),
                TargetType.CANNON,
                null, // No EntityType for a cannon
                cannon.getUID(),
                getCannonRandom(cannon, 0.5, 0.0, 0.5), //ground
                getCannonRandom(cannon, 0.5, 0.5, 0.5), //center
                cannon.getVelocity()
        );
    }

    protected static <T extends CannonPositionHolder<?>> Coordinate getCannonRandom(T cannon, double x, double y, double z) {
        var center = cannon.getRandomCoordinate().clone();
        center.getVector().add(new CannonVector(x, y, z));
        return center;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof Target<?> target)) {
            return false;
        }

        return Objects.equals(this.name, target.name) &&
                Objects.equals(this.targetType, target.targetType) &&
                Objects.equals(this.type, target.type) &&
                Objects.equals(this.uniqueId, target.uniqueId) &&
                Objects.equals(this.groundLocation, target.groundLocation) &&
                Objects.equals(this.centerLocation, target.centerLocation) &&
                Objects.equals(this.velocity, target.velocity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, targetType, type, uniqueId, groundLocation, centerLocation, velocity);
    }

    @Override
    public String toString() {
        return "Target[" +
                "name=" + name + ", " +
                "targetType=" + targetType + ", " +
                "type=" + type + ", " +
                "uniqueId=" + uniqueId + ", " +
                "groundLocation=" + groundLocation + ", " +
                "centerLocation=" + centerLocation + ", " +
                "velocity=" + velocity + ']';
    }

}