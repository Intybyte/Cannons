package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.Key;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@Builder
@ConfigSerializable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DefaultProjectileDefinition implements ProjectilePhysics {
    private @NotNull Key key; // for the entity type
    private Double constantAcceleration;
    private double gravity;
    private double drag;
    private double waterDrag;

    @Override
    public Key getEntityKey() {
        return key;
    }
}
