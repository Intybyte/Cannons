package at.pavlov.cannons.schematic;

import at.pavlov.cannons.Cannons;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class SchemUtil {
    public static Block coord1 = null;
    public static Block coord2 = null;

    public static final NamespacedKey CANNON_KEY = new NamespacedKey(Cannons.getPlugin(), "selector");
    public static final ItemStack SELECT_TOOL = getSelectTool();
    private static ItemStack getSelectTool() {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            meta = Bukkit.getItemFactory().getItemMeta(Material.BLAZE_ROD);
        }

        meta.setDisplayName(ChatColor.RESET + "Cannon Select Tool");
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.getPersistentDataContainer().set(CANNON_KEY, PersistentDataType.BOOLEAN, true);

        item.setItemMeta(meta);
        return item;
    }
}
