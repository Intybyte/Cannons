package at.pavlov.internal.armor;

import at.pavlov.internal.Key;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

public interface BaseArmorHolder {
    Random random = new Random();

    @NotNull BaseArmorPiece getHelmet();
    @NotNull BaseArmorPiece getChestplate();
    @NotNull BaseArmorPiece getLeggings();
    @NotNull BaseArmorPiece getBoots();

    double getArmor();
    double getArmorToughness();

    default BaseArmorPiece[] getArmorPieces() {
        return new BaseArmorPiece[] {getHelmet(), getChestplate(), getLeggings(), getBoots()};
    }

    default Stream<BaseArmorPiece> getStream() {
        return Arrays.stream(getArmorPieces());
    }

    //http://www.minecraftwiki.net/wiki/Armor#Armor_enchantment_effect_calculation
    default double getEnchantProtection(Key key) {
        double reduction = getStream()
                .mapToDouble(piece -> piece.getEnchantProtectionReduction(key))
                .sum();

        reduction = Math.min(reduction, 25);
        reduction *= (random.nextFloat() * 0.5 + 0.5);

        // Final cap to 20
        reduction = Math.min(reduction, 20);

        return reduction * 0.04; // 1 point = 4%
    }

    default double getArmorDamageReduced(double damage) {
        double armor = getArmor();
        double toughness = getArmorToughness();

        double reductionPoints = Math.min(
                20.0,
                Math.max(armor / 5.0, armor - damage / (2.0 + toughness / 4.0))
        );

        return reductionPoints / 25.0; // Convert to [0.0 - 0.8] range
    }

    default double getDirectHitReduction(double armorPiercing, double damage) {
        double overallPiercing = armorPiercing + 1;
        return (1 - getArmorDamageReduced(damage) / overallPiercing) * (1 - getEnchantProtection(Key.mc("protection")) / overallPiercing);
    }

    default double getExplosionHitReduction(double armorPiercing, double damage) {
        double overallPiercing = armorPiercing + 1;
        return (1 - getArmorDamageReduced(damage) / overallPiercing) * (1 - getEnchantProtection(Key.mc("blast_protection")));
    }

    default void damageArmor() {
        getStream().forEach(BaseArmorPiece::damage);
    }
}
