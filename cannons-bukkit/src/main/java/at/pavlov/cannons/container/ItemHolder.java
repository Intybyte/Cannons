package at.pavlov.cannons.container;

import at.pavlov.cannons.multiversion.VersionHandler;
import at.pavlov.cannons.utils.ParseUtils;
import at.pavlov.cannons.utils.StringUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ItemHolder {
    private Material type;
    @NotNull private final String displayName;
    @NotNull private final List<String> lore;

    public ItemHolder(ItemStack item) {
        if (item == null) {
            type = Material.AIR;
            displayName = "";
            lore = new ArrayList<>();
            return;
        }

        type = item.getType();

        if (!item.hasItemMeta()) {
            displayName = "";
            lore = new ArrayList<>();
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta.hasDisplayName() && meta.getDisplayName() != null) {
            displayName = meta.getDisplayName();
        } else if (VersionHandler.isGreaterThan1_20_5() && meta.hasItemName()) {
            displayName = meta.getItemName();
        } else if (!meta.hasDisplayName()) {
            displayName = getFriendlyName(item);
        } else {
            displayName = "";
        }

        boolean loreExists = meta.hasLore() && meta.getLore() != null;
        lore = loreExists ? meta.getLore() : new ArrayList<>();
    }

    public ItemHolder(Material material) {
        this(material, null, null);
    }

    public ItemHolder(Material material, String description, List<String> lore) {
        this.type = Objects.requireNonNullElse(material, Material.AIR);
        this.displayName = description == null ? "" : ChatColor.translateAlternateColorCodes('&', description);
        this.lore = Objects.requireNonNullElseGet(lore, ArrayList::new);
    }

    public ItemHolder(String str) {
        // data structure:
        // id;DESCRIPTION;LORE1;LORE2
        // HOE;COOL Item;Looks so cool;Fancy
        String[] entries = str.split(";");
		lore = new ArrayList<>();
        if (entries.length > 0) {
            type = Material.matchMaterial(entries[0]);
            if (type == null) {
                type = Material.AIR;
            }
        }

        if (entries.length > 1) {
            displayName = entries[1].replace('&', '§');
        } else {
            displayName = "";
        }

        if (entries.length > 2) {
            lore.addAll(Arrays.asList(entries).subList(2, entries.length));
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
    public boolean equals(Material material) {
        return material != null && material.equals(this.type);
    }

    /**
     * compares id and data, but skips data comparison if one is -1
     *
     * @param item item to compare
     * @return true if both items are equal in data and id or only the id if one data = -1
     */
    public boolean equalsFuzzy(ItemStack item) {
        ItemHolder itemHolder = new ItemHolder(item);
        return equalsFuzzy(itemHolder);
    }


    /**
     * compares id and data, but skips data comparison if one is -1
     *
     * @param item the item to compare
     * @return true if both items are equal in data and id or only the id if one data = -1
     */
    public boolean equalsFuzzy(ItemHolder item) {
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

    public String toString() {
        return this.type + ":" + this.displayName + ":" + StringUtils.join(this.lore, ":");
    }

    public boolean hasDisplayName() {
        return !this.displayName.isEmpty();
    }

    public boolean hasLore() {
        return !this.lore.isEmpty();
    }

    public static @NotNull String getFriendlyName(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) return "Air";

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return itemStack.getItemMeta().getDisplayName();
        }

        return ParseUtils.normalizeName(itemStack.getType().name());
    }
}