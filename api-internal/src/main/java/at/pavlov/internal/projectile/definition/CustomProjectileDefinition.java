package at.pavlov.internal.projectile.definition;

import at.pavlov.internal.key.Key;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

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

    //for throwable projectiles only
    private final @Nullable Key material;
    private final @Nullable Integer customModelData;

    /*
    @Override
    public double getGravity() {
        return fromKey().getGravity();
    }

    @Override
    public double getDrag() {
        return fromKey().getDrag();
    }

    @Override
    public double getWaterDrag() {
        return fromKey().getWaterDrag();
    }

    private @NotNull ProjectilePhysics fromKey() {
        DefaultProjectileDefinition value = Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.of(entityKey);
        if (value == null) {
            return ProjectilePhysics.DEFAULT;
        }

        return value;
    }*/
}
