package at.pavlov.cannons.dao.wrappers;

import at.pavlov.cannons.Enum.ProjectileCause;
import at.pavlov.cannons.cannon.Cannon;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Supplies BaseFireTask depending on arguments, for instance you can have the cannons to shoot
 * another plugin's custom projectile in some conditions, in other just return the previous event
 * or the default task creator
 */
@FunctionalInterface
public interface FireTaskCreator {
    @Nullable BaseFireTask create(Cannon cannon, UUID shooter, boolean removeCharge, ProjectileCause projectileCause);
}
