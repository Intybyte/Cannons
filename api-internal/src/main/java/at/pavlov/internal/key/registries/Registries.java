package at.pavlov.internal.key.registries;

import at.pavlov.internal.Key;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.DefaultProjectileDefinition;
import at.pavlov.internal.projectile.definition.KeyedDefaultProjectile;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Registries {
    public static final Registry.Composite<ProjectilePhysics> PROJECTILE_PHYSICS;
    public static final Registry<CustomProjectileDefinition> CUSTOM_PROJECTILE_DEFINITION;
    public static final Registry<ProjectilePhysics> DEFAULT_PROJECTILE_DEFINITION_REGISTRY;

    static {
        CUSTOM_PROJECTILE_DEFINITION = new Registry<>();
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY = new Registry<>(() -> {
            List<ProjectilePhysics> toAdd = new ArrayList<>();

            Collection<Key> fireballs = Key.from(
                    List.of(
                        "minecraft:fireball",
                        "minecraft:small_fireball",
                        "minecraft:dragon_fireball",
                        "minecraft:wither_skull",
                        "minecraft:shulker_bullet")
            );
            var fireballBuilder = DefaultProjectileDefinition.builder()
                    .gravity(0.0)
                    .drag(0.95)
                    .waterDrag(0.95)
                    .constantAcceleration(0.1);
            for (Key fireball : fireballs) {
                toAdd.add(
                        fireballBuilder
                                .key(fireball)
                                .build()
                );
            }

            Collection<Key> arrows = Key.from(List.of("minecraft:arrow", "minecraft:spectral_arrow"));
            var arrowBuilder = DefaultProjectileDefinition.builder()
                    .gravity(0.05)
                    .drag(0.99)
                    .waterDrag(0.6);
            for (Key arrow : arrows) {
                toAdd.add(
                        arrowBuilder
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

            Key.from(
                List.of(
                    "minecraft:snowball",
                    "minecraft:egg",
                    "minecraft:ender_pearl",
                    "minecraft:experience_bottle",
                    "minecraft:potion",
                    "minecraft:lingering_potion",
                    "minecraft:llama_spit"
                )
            ).forEach( entry -> toAdd.add(
                new KeyedDefaultProjectile(entry)
            ));

            return toAdd;
        });
        SharedRegistryKeyValidator<ProjectilePhysics> validator = new SharedRegistryKeyValidator<>(CUSTOM_PROJECTILE_DEFINITION, DEFAULT_PROJECTILE_DEFINITION_REGISTRY);
        CUSTOM_PROJECTILE_DEFINITION.setValidator(validator);
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setValidator(validator);
        DEFAULT_PROJECTILE_DEFINITION_REGISTRY.setFrozen(true);

        PROJECTILE_PHYSICS = new Registry.Composite<>(CUSTOM_PROJECTILE_DEFINITION, DEFAULT_PROJECTILE_DEFINITION_REGISTRY);
    }
}
