package at.pavlov.bukkit.container;

import at.pavlov.bukkit.factory.CoordinateUtil;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.internal.cannons.holders.CannonMainDataHolder;
import at.pavlov.internal.cannons.holders.CannonPositionHolder;
import at.pavlov.internal.container.Target;
import at.pavlov.internal.enums.TargetType;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

public final class BukkitTarget extends Target<EntityType> {

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

    public BukkitTarget(Entity entity) {
        super(
                entity.getName(),
                entityToTarget(entity),
                entity.getType(),
                entity.getUniqueId(),
                CoordinateUtil.fromLoc(entity.getLocation().clone()),
                CoordinateUtil.fromLoc(
                        entity instanceof LivingEntity livingEntity ?
                        livingEntity.getEyeLocation().clone().add(0.0, -0.5, 0.0) :
                        entity.getLocation().clone().add(0.0, 0.5, 0.0)
                ),
                VectorUtils.fromBaseVector(entity.getVelocity())
        );
    }

    public <T extends CannonMainDataHolder & CannonPositionHolder<?>> BukkitTarget(T cannon) {
        super(cannon);
    }
}