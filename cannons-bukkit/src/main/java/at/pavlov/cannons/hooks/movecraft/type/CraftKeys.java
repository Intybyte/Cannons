package at.pavlov.cannons.hooks.movecraft.type;

import at.pavlov.cannons.Cannons;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.NamespacedKey;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CraftKeys {
    public static final NamespacedKey MAX_CANNONS = Cannons.nsKey("max_cannons");
    public static final NamespacedKey MIN_CANNONS = Cannons.nsKey("min_cannons");

    public static final NamespacedKey MAX_MASS = Cannons.nsKey("max_mass");
    public static final NamespacedKey MIN_MASS = Cannons.nsKey("min_mass");
    public static final NamespacedKey EXCLUDE_FROM_MASS = Cannons.nsKey("exclude_from_mass");

    public static final NamespacedKey USE_SHIP_ANGLES = Cannons.nsKey("use_ship_angles");
}
