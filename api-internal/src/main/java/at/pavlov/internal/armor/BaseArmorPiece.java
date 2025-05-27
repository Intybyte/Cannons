package at.pavlov.internal.armor;

import at.pavlov.internal.Key;

public interface BaseArmorPiece {
    BaseArmorPiece EMPTY = new BaseArmorPiece() {
        @Override
        public double getEnchantProtectionReduction(Key key) {
            return 0;
        }

        @Override
        public void damage() {
            //nothing to damage
        }
    };

    double getEnchantProtectionReduction(Key key);
    void damage();
}
