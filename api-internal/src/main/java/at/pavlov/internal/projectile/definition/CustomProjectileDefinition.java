package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.key.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CustomProjectileDefinition implements ProjectilePhysics {
    private final Key key; // for the name of the custom projectile definition
    private final Key entityKey;

    private final Double constantAcceleration;
    private final double gravity;
    private final double drag;
    private final double waterDrag;
}
