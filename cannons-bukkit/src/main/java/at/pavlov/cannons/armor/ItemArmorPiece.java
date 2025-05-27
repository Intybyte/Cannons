package at.pavlov.cannons.armor;

import at.pavlov.internal.Key;
import at.pavlov.internal.armor.BaseArmorHolder;
import at.pavlov.internal.armor.BaseArmorPiece;
import at.pavlov.internal.armor.NoArmorPiece;
import com.cryptomorin.xseries.XEnchantment;
import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

@Getter
public class ItemArmorPiece implements BaseArmorPiece {
    private final ItemStack stack;
    private ItemArmorPiece(ItemStack stack) {
        Preconditions.checkArgument(stack.getItemMeta() != null);
        Preconditions.checkArgument(stack.getItemMeta() instanceof Damageable);
        this.stack = stack;
    }

    public static @NotNull BaseArmorPiece of(ItemStack stack) {
        if (stack == null) return new NoArmorPiece();
        if (stack.getType().isAir()) return new NoArmorPiece();
        return new ItemArmorPiece(stack);
    }

    @Override
    public double getEnchantProtectionReduction(Key key) {
        int reduction = 0;

        Enchantment protection = XEnchantment.PROTECTION.get();
        Enchantment ourEnchant = XEnchantment.of(key.full()).orElseThrow().get();
        int lvl = stack.getEnchantmentLevel(ourEnchant);
        if (lvl > 0) {
            reduction += (int) Math.floor((6 + lvl * lvl) * 1.5 / 3);
        }

        if (!ourEnchant.equals(protection)) {
            lvl = stack.getEnchantmentLevel(protection);
            if (lvl > 0) {
                reduction += (int) Math.floor((6 + lvl * lvl) * 0.75 / 3);
            }
        }

        return reduction;
    }

    @Override
    public void damage() {
        int lvl = stack.getEnchantmentLevel(XEnchantment.UNBREAKING.get());
        //chance of breaking in 0-1
        double breakingChance = 0.6+0.4/(lvl+1);

        if (BaseArmorHolder.random.nextDouble() < breakingChance) {
            Damageable itemMeta = (Damageable) stack.getItemMeta();
            itemMeta.setDamage(itemMeta.getDamage() + 1);
        }
    }
}
