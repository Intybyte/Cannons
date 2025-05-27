package at.pavlov.internal.armor;

import at.pavlov.internal.Key;

public class NoArmorPiece implements BaseArmorPiece {
    @Override
    public double getEnchantProtectionReduction(Key key) {
        return 0;
    }

    @Override
    public void damage() {
        //nothing to damage
    }
}
