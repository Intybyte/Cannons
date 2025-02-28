package at.pavlov.cannons.dao.wrappers;

import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;

public record RemoveTaskWrapper(
        Cannon cannon,
        boolean breakCannon,
        boolean canExplode,
        BreakCause cause,
        boolean removeEntry,
        boolean ignoreInvalid
) {
}
