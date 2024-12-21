package at.pavlov.cannons.dao.wrappers;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.internal.enums.ProjectileCause;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data public class FireTaskWrapper {
    private Cannon cannon;
    private UUID player;
    private boolean removeCharge;
    private final ProjectileCause projectileCause;
}
