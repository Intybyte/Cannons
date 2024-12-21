package at.pavlov.cannons.dao.wrappers;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.internal.enums.BreakCause;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data public class RemoveTaskWrapper {
	private Cannon cannon;
    private boolean breakCannon;
    private boolean canExplode;
    private BreakCause cause;
    private boolean removeEntry;
    private boolean ignoreInvalid;
}
