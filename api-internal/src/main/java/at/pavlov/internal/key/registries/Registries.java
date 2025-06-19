package at.pavlov.internal.key.registries;

import at.pavlov.internal.key.Key;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.DefaultProjectileDefinition;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Registries {
    public static final Registry.Composite<ProjectilePhysics> PROJECTILE_PHYSICS;
    public static final Registry<CustomProjectileDefinition> CUSTOM_PROJECTILE_DEFINITION;
    public static final Registry<DefaultProjectileDefinition> DEFAULT_PROJECTILE_DEFINITION_REGISTRY;

    static {
        CUSTOM_PROJECTILE_DEFINITION = new Registry<>();
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY = new Registry<>(() -> {
            List<DefaultProjectileDefinition> toAdd = new ArrayList<>();

            Collection<Key> fireballs = Key.from(
                    List.of("minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball", "minecraft:wither_skull", "minecraft:shulker_bullet")
            );
            for (Key fireball : fireballs) {
                toAdd.add(
                        DefaultProjectileDefinition.builder()
                                .gravity(0.0)
                                .drag(0.95)
                                .waterDrag(0.95)
                                .constantAcceleration(0.1)
                                .key(fireball)
                                .build()
                );
            }

            Collection<Key> arrows = Key.from(List.of("minecraft:arrow", "minecraft:spectral_arrow"));
            for (Key arrow : arrows) {
                toAdd.add(
                        DefaultProjectileDefinition.builder()
                                .gravity(0.05)
                                .drag(0.99)
                                .waterDrag(0.6)
                                .key(arrow)
                                .build()
                );
            }

            toAdd.add(
                    DefaultProjectileDefinition.builder()
                            .gravity(0.05)
                            .drag(0.99)
                            .waterDrag(0.99)
                            .key(Key.mc("trident"))
                            .build()
            );

            toAdd.add(
                    DefaultProjectileDefinition.builder()
                            .gravity(0.0)
                            .drag(1.0)
                            .waterDrag(1.0)
                            .key(Key.mc("breeze_wind_charge"))
                            .build()
            );

            return toAdd;
        });
        SharedRegistryKeyValidator<ProjectilePhysics> validator = new SharedRegistryKeyValidator<>(CUSTOM_PROJECTILE_DEFINITION, DEFAULT_PROJECTILE_DEFINITION_REGISTRY);
        CUSTOM_PROJECTILE_DEFINITION.setValidator(validator);
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setValidator(validator);
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setFrozen(true);

        PROJECTILE_PHYSICS = new Registry.Composite<>(CUSTOM_PROJECTILE_DEFINITION, DEFAULT_PROJECTILE_DEFINITION_REGISTRY);
    }
}
