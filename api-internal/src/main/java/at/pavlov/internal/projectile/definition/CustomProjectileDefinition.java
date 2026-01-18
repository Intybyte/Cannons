package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

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

    private final boolean glowing;

    private final boolean onFire; // visual fire for projectile
    private final boolean charged; // works for wither skeletons
    private final boolean critical; // for arrows and tridents

    //for throwable projectiles & item displays only
    private final @Nullable Key material;
    private final @Nullable Integer customModelData;

    private final @NotNull Map<String, @NotNull Double> attributes;

    private final @NotNull Map<String, @NotNull Double> entityDisplayData;
}
