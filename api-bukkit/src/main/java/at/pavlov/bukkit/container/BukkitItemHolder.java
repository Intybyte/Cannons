package at.pavlov.bukkit.container;

import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.container.ItemHolder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

//small class as at.pavlov.cannons.container for item id and data
public class BukkitItemHolder extends ItemHolder<Material> {

    public static BukkitItemHolder from(ItemStack item) {
        if (item == null) {
            return new BukkitItemHolder(Material.AIR);
        }

        Material material = item.getType();
        String displayName;
        List<String> lore;

        if (!item.hasItemMeta()) {
            return new BukkitItemHolder(Material.AIR);
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName() && meta.getDisplayName() != null) {
            displayName = meta.getDisplayName();
        } else if (!meta.hasDisplayName()) {
            displayName = getFriendlyName(item, true);
            //Cannons.getPlugin().logDebug("display name: " + displayName);
        } else {
            displayName = "";
        }

        boolean loreExists = meta.hasLore() && meta.getLore() != null;
        lore = loreExists ? meta.getLore() : new ArrayList<>();
        return new BukkitItemHolder(material, displayName, lore);
    }

    public BukkitItemHolder(Material material) {
        this(material, null, null);
    }

    public BukkitItemHolder(Material material, String description, List<String> lore) {
        super(material, description, lore);
    }

    @Override
    public Material defaultType() {
        return Material.AIR;
    }

    public static BukkitItemHolder from(String str) {
        // data structure:
        // id;DESCRIPTION;LORE1;LORE2
        // HOE;COOL Item;Looks so cool;Fancy
        try {
            Material material = Material.AIR;
            Scanner s = new Scanner(str).useDelimiter("\\s*;\\s*");

            if (s.hasNext()) {
                String next = s.next();
                if (next != null)
                    material = Material.matchMaterial(next);
                if (material == null) {
                    material = Material.AIR;
                }
            }

            String displayName = s.hasNext() ? ChatColor.translateAlternateColorCodes('&', s.next()) : "";
            List<String> lore = new ArrayList<>();

            while (s.hasNext()) {
                String nextStr = s.next();
                if (!nextStr.isEmpty())
                    lore.add(nextStr);
            }

            s.close();
            return new BukkitItemHolder(material, displayName, lore);
        } catch (Exception e) {
            CannonLogger.getLogger().log(Level.SEVERE, "[CANNONS] Error while converting " + str + ". Check formatting (minecraft:clock)");
            return new BukkitItemHolder(Material.AIR);
        }
    }

    public ItemStack toItemStack(int amount) {
        type = type == null ? Material.AIR : type;
        ItemStack item = new ItemStack(type, amount);
        ItemMeta meta = item.getItemMeta();
        if (this.hasDisplayName())
            meta.setDisplayName(this.displayName);
        if (this.hasLore())
            meta.setLore(this.lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * compares the id of two Materials
     *
     * @param material material to compare
     * @return true if both material are equal
     */
    public boolean check(Material material) {
        return material != null && material.equals(this.type);
    }

    /**
     * compares id and data, but skips data comparison if one is -1
     *
     * @param item item to compare
     * @return true if both items are equal in data and id or only the id if one data = -1
     */
    public boolean equalsFuzzy(ItemStack item) {
        BukkitItemHolder itemHolder = BukkitItemHolder.from(item);
        return equalsFuzzy(itemHolder);
    }


    /**
     * compares id and data, but skips data comparison if one is -1
     *
     * @param item the item to compare
     * @return true if both items are equal in data and id or only the id if one data = -1
     */
    public boolean equalsFuzzy(BukkitItemHolder item) {
        if (item == null) {
            return false;
        }

        //System.out.println("item: " + item.getDisplayName() + " cannons " + this.getDisplayName());
        //Item does not have the required display name
        if ((this.hasDisplayName() && !item.hasDisplayName()) || (!this.hasDisplayName() && item.hasDisplayName()))
            return false;

        //Display name do not match
        if (item.hasDisplayName() && this.hasDisplayName() && !item.getDisplayName().equals(displayName))
            return false;

        if (!this.hasLore()) {
            return item.getType().equals(this.type);
        }
        //does Item have a Lore
        if (!item.hasLore())
            return false;

        Collection<String> similar = new HashSet<>(this.lore);

        int size = similar.size();
        similar.retainAll(item.getLore());

        if (similar.size() < size)
            return false;

        return item.getType().equals(this.type);
    }

    private static String capitalizeFully(String name) {
        if (name == null) {
            return "";
        }

        if (name.length() <= 1) {
            return name.toUpperCase();
        }

        if (!name.contains("_")) {
            return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        StringBuilder sbName = new StringBuilder();
        for (String subName : name.split("_"))
            sbName.append(subName.substring(0, 1).toUpperCase() + subName.substring(1).toLowerCase()).append(" ");

        return sbName.toString().substring(0, sbName.length() - 1);
    }

    private static String getFriendlyName(Material material) {
        return material == null ? "Air" : getFriendlyName(new ItemStack(material), false);
    }

    private static String getFriendlyName(ItemStack itemStack, boolean checkDisplayName) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return "Air";

        if (checkDisplayName && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }

        return capitalizeFully(itemStack.getType().name().replace("_", " ").toLowerCase());
    }
}