package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.utils.ParseUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class ItemNameResolver {
    private ItemNameResolver() {}

    public static String getName(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName() && meta.getDisplayName() != null) {
            return meta.getDisplayName();
        } else if (meta.hasItemName()) {
            return meta.getItemName();
        } else if (!meta.hasDisplayName()) {
            return getFriendlyName(item);
        } else {
            return "";
        }
    }

    public static @NotNull String getFriendlyName(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return "Air";

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }

        return ParseUtils.normalizeName(itemStack.getType().name());
    }
}
