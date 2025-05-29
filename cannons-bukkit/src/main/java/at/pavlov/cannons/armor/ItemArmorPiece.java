package at.pavlov.cannons.armor;

import at.pavlov.internal.Key;
import at.pavlov.internal.armor.BaseArmorHolder;
import at.pavlov.internal.armor.BaseArmorPiece;
import com.cryptomorin.xseries.XEnchantment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemArmorPiece implements BaseArmorPiece {
    private final @NotNull ItemStack stack;
    public static @NotNull BaseArmorPiece of(ItemStack stack) {
        if (stack == null) return BaseArmorPiece.EMPTY;
        if (stack.getType().isAir()) return BaseArmorPiece.EMPTY;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return BaseArmorPiece.EMPTY;
        if (!(meta instanceof Damageable)) return BaseArmorPiece.EMPTY;

        return new ItemArmorPiece(stack);
    }

    @Override
    public void damage() {
        if (BaseArmorHolder.random.nextDouble() < getBreakingChance()) {
            Damageable itemMeta = (Damageable) stack.getItemMeta();
            itemMeta.setDamage(itemMeta.getDamage() + 1);
        }
    }

    @Override
    public int getEnchantmentLevel(Key key) {
        Enchantment enchant = XEnchantment.of(key.full()).orElseThrow().get();
        return stack.getEnchantmentLevel(enchant);
    }
}
