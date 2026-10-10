package at.pavlov.cannons.hooks.windfarer.properties;

import at.pavlov.cannons.Cannons;
import net.countercraft.movecraft.craft.type.PropertyKey;
import net.countercraft.movecraft.craft.type.PropertyKeyTypes;
import net.countercraft.movecraft.craft.type.PropertyKeys;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.List;

public class CannonCraftTypeProperties {

    private CannonCraftTypeProperties() {
        // Prevent initialization
    }

    // Simple types
    public static final PropertyKey<Integer> MIN_CANNON_MASS = PropertyKeyTypes.intPropertyKey(new NamespacedKey(Cannons.getPlugin(), "mass/min"), 0);
    public static final PropertyKey<Integer> MAX_CANNON_MASS = PropertyKeyTypes.intPropertyKey(new NamespacedKey(Cannons.getPlugin(), "mass/max"), 0);

    public static final PropertyKey<Boolean> USE_SHIP_ANGLES = PropertyKeyTypes.boolPropertyKey(new NamespacedKey(Cannons.getPlugin(), "use_ship_angles"), false);

    // Object types
    public static final PropertyKey<List<String>> EXCLUDE_FROM_MASS = PropertyKeyTypes.stringListPropertyKey(new NamespacedKey(Cannons.getPlugin(), "mass/excluded"), List.of());
    public static final PropertyKey<List<CannonTypeConstraint>> CANNON_TYPE_CONSTRAINTS = PropertyKeys.register(
            new PropertyKey<>(
                    new NamespacedKey(Cannons.getPlugin(), "cannon_constraints"),
                    typeSafeCraftType -> List.of(),
                    (yamlObject, type) -> {
                        return switch(yamlObject) {
                            case CannonTypeConstraint ctc -> List.of(ctc);
                            case List<?> rawList -> {
                                List<CannonTypeConstraint> result = new ArrayList<>();
                                for (Object object : rawList) {
                                    if (object instanceof CannonTypeConstraint ctc) {
                                        result.add(ctc);
                                    }
                                }
                                yield result;
                            }
                            default -> List.of();
                        };
                    },
                    (t) -> t,
                    (list) -> {
                        List<CannonTypeConstraint> newList = new ArrayList<>(list.size());
                        for (CannonTypeConstraint ctc : list) {
                            newList.add(new CannonTypeConstraint(ctc));
                        }
                        return newList;
                    }
            )
    );

    public static void register() {
        ConfigurationSerialization.registerClass(CannonTypeConstraint.class, "CannonsRevamped_CannonTypeConstraint");
    }
}
