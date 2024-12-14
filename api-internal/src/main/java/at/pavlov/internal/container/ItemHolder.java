package at.pavlov.internal.container;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

//small class as at.pavlov.cannons.container for item id and data
@Getter
abstract public class ItemHolder<Type> {
    protected Type type;
    protected String displayName;
    protected List<String> lore;

    public ItemHolder(Type material) {
        this(material, null, null);
    }

    public ItemHolder(Type material, String description, List<String> lore) {
        this.type = Objects.requireNonNullElseGet(material, this::defaultType);
        this.displayName = Objects.requireNonNullElse(description, "");
        this.lore = Objects.requireNonNullElseGet(lore, ArrayList::new);
    }

    abstract public Type defaultType();

    /**
     * compares the id of two Materials
     *
     * @param material material to compare
     * @return true if both material are equal
     */
    public boolean check(Type material) {
        return material != null && material.equals(this.type);
    }


    /**
     * compares id and data, but skips data comparison if one is -1
     *
     * @param item the item to compare
     * @return true if both items are equal in data and id or only the id if one data = -1
     */
    public boolean equalsFuzzy(ItemHolder<?> item) {
        if (item == null) {
            return false;
        }

        //System.out.println("item: " + item.getDisplayName() + " cannons " + this.getDisplayName());
        //Item does not have the required display name
        if (!this.hasDisplayName() && item.hasDisplayName()) {
            return false;
        }

        if (this.hasDisplayName() && !item.hasDisplayName()) {
            return false;
        }

        //Display name do not match
        if (item.hasDisplayName() && this.hasDisplayName() && !item.getDisplayName().equals(displayName))
            return false;

        if (!this.hasLore()) {
            return item.getType().equals(this.type);
        }
        //does Item have a Lore
        if (!item.hasLore()) {
            return false;
        }

        Collection<String> similar = new HashSet<>(this.lore);

        int size = similar.size();
        similar.retainAll(item.getLore());

        if (similar.size() < size)
            return false;

        return item.getType().equals(this.type);
    }

    public String toString() {
        return this.type + ":" + this.displayName + ":" + String.join(":", this.lore);
    }

    public void setType(Type material) {
        this.type = material;
    }

    public boolean hasDisplayName() {
        return this.displayName != null && !this.displayName.isEmpty();
    }

    public boolean hasLore() {
        return this.lore != null && !this.lore.isEmpty();
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
            sbName.append(subName.substring(0, 1).toUpperCase())
                    .append(subName.substring(1).toLowerCase())
                    .append(" ");

        return sbName.substring(0, sbName.length() - 1);
    }
}