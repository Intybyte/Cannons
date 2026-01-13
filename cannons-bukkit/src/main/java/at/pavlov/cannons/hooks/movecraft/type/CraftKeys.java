package at.pavlov.cannons.hooks.movecraft.type;

import org.bukkit.NamespacedKey;

@SuppressWarnings("all")
public class CraftKeys {
    public static final NamespacedKey MAX_CANNONS = new NamespacedKey("cannons_revamped", "max_cannons");
    public static final NamespacedKey MIN_CANNONS = new NamespacedKey("cannons_revamped", "min_cannons");

    public static final NamespacedKey MAX_MASS = new NamespacedKey("cannons_revamped", "max_mass");
    public static final NamespacedKey MIN_MASS = new NamespacedKey("cannons_revamped", "min_mass");
    public static final NamespacedKey EXCLUDE_FROM_MASS = new NamespacedKey("cannons_revamped", "exclude_from_mass");

    public static final NamespacedKey USE_SHIP_ANGLES = new NamespacedKey("cannons_revamped", "use_ship_angles");
}
