package at.pavlov.internal.armor;

import at.pavlov.internal.Key;

public interface BaseArmorPiece {
    BaseArmorPiece EMPTY = new BaseArmorPiece() {

        @Override
        public void damage() {
            //nothing to damage
        }

        @Override
        public int getEnchantmentLevel(Key key) {
            return 0;
        }
    };

    void damage();
    int getEnchantmentLevel(Key key);

    default double getEnchantProtectionReduction(Key specialEnchantKey) {
        int reduction = 0;

        int lvl = this.getEnchantmentLevel(specialEnchantKey);
        if (lvl > 0) {
            reduction += (int) Math.floor((6 + lvl * lvl) * 1.5 / 3);
        }

        Key protectionKey = Key.mc("protection");
        if (!specialEnchantKey.equals(protectionKey)) {
            lvl = this.getEnchantmentLevel(protectionKey);
            if (lvl > 0) {
                reduction += (int) Math.floor((6 + lvl * lvl) * 0.75 / 3);
            }
        }

        return reduction;
    }

    default double getBreakingChance() {
        int lvl = this.getEnchantmentLevel(Key.mc("unbreaking"));
        //chance of breaking in 0-1
        return 0.6 + 0.4 / (lvl + 1);
    }
}
