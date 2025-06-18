package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
@Builder
@AllArgsConstructor
public class DefaultProjectileDefinition implements ProjectilePhysics {
    private final @NotNull Key key; // for the entity type
    private final Double constantAcceleration;
    private final double gravity;
    private final double drag;
    private final double waterDrag;

    @Override
    public Key getEntityKey() {
        return key;
    }
}
