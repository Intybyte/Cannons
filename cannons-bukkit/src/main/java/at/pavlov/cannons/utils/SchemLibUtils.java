package at.pavlov.cannons.utils;

import me.vaan.schematiclib.base.block.BlockKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class SchemLibUtils {
    public static BlockKey materialKey(@NotNull Material mat) {
        NamespacedKey key = mat.getKey();
        return BlockKey.mc(key.getKey());
    }
}
