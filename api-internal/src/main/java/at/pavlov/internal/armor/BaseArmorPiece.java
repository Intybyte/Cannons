package at.pavlov.internal.armor;

import at.pavlov.internal.Key;

public interface BaseArmorPiece {
    double getEnchantProtectionReduction(Key key);
    void damage();
}
