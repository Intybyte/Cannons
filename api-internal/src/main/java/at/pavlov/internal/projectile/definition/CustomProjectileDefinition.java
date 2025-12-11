package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.Key;
import lombok.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@Getter
@Builder
@EqualsAndHashCode
@ConfigSerializable
@NoArgsConstructor
@AllArgsConstructor
public class CustomProjectileDefinition implements ProjectilePhysics {
    private Key key; // for the name of the custom projectile definition
    private Key entityKey;

    private Double constantAcceleration;

    private double gravity;
    private double drag;
    private double waterDrag;

    private boolean glowing;

    private boolean onFire; // visual fire for projectile
    private boolean charged; // works for wither skeletons
    private boolean critical; // for arrows and tridents

    //for throwable projectiles only
    private @Nullable Key material;
    private @Nullable Integer customModelData;
}
