package at.pavlov.cannons.container;

import at.pavlov.cannons.Enum.TargetType;
import at.pavlov.cannons.cannon.Cannon;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.util.UUID;

public record Target(
        String name,
        TargetType targetType,
        EntityType type,
        UUID uniqueId,
        Location groundLocation,
        Location centerLocation,
        Vector velocity
) {

    public static TargetType entityToTarget(Entity entity) {
        if (entity instanceof Player) {
            return TargetType.PLAYER;
        }

        if (entity instanceof Monster) {
            return TargetType.MONSTER;
        }

        if (entity instanceof Animals) {
            return TargetType.ANIMAL;
        }

        return TargetType.OTHER;
    }

    public Target(Entity entity) {
        this(
            entity.getName(),
            entityToTarget(entity),
            entity.getType(),
            entity.getUniqueId(),
            entity.getLocation().clone(),
            entity instanceof LivingEntity livingEntity ?
                    livingEntity.getEyeLocation().clone().add(0.0, -0.5, 0.0) :
                    entity.getLocation().clone().add(0.0, 0.5, 0.0),
            entity.getVelocity()
        );
    }

    public Target(Cannon cannon) {
        this(
            cannon.getCannonName(),
            TargetType.CANNON,
            null, // No EntityType for a cannon
            cannon.getUID(),
            cannon.getRandomBarrelBlock().clone().add(0.5, 0.0, 0.5),
            cannon.getRandomBarrelBlock().clone().add(0.5, 0.5, 0.5),
            cannon.getVelocity()
        );
    }
}