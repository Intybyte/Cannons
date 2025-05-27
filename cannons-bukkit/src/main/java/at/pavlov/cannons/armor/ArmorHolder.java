package at.pavlov.cannons.armor;

import at.pavlov.internal.armor.BaseArmorHolder;
import at.pavlov.internal.armor.BaseArmorPiece;
import at.pavlov.internal.armor.NoArmorPiece;
import com.cryptomorin.xseries.XAttribute;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor
public class ArmorHolder implements BaseArmorHolder {
    private final LivingEntity living;

    @Override
    public @NotNull BaseArmorPiece getHelmet() {
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) {
            return new NoArmorPiece();
        }

        ItemStack stack = equipment.getHelmet();
        return ItemArmorPiece.of(stack);
    }

    @Override
    public @NotNull BaseArmorPiece getChestplate() {
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) {
            return new NoArmorPiece();
        }

        ItemStack stack = equipment.getChestplate();
        return ItemArmorPiece.of(stack);
    }

    @Override
    public @NotNull BaseArmorPiece getLeggings() {
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) {
            return new NoArmorPiece();
        }

        ItemStack stack = equipment.getLeggings();
        return ItemArmorPiece.of(stack);
    }

    @Override
    public @NotNull BaseArmorPiece getBoots() {
        EntityEquipment equipment = living.getEquipment();
        if (equipment == null) {
            return new NoArmorPiece();
        }

        ItemStack stack = equipment.getBoots();
        return ItemArmorPiece.of(stack);
    }

    @Override
    public double getArmor() {
        AttributeInstance attribute = living.getAttribute(XAttribute.ARMOR.get());
        if (attribute == null) {
            return 0.0;
        }

        return attribute.getValue();
    }

    @Override
    public double getArmorToughness() {
        AttributeInstance attribute = living.getAttribute(XAttribute.ARMOR_TOUGHNESS.get());
        if (attribute == null) {
            return 0.0;
        }

        return attribute.getValue();
    }
}
