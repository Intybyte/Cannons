package at.pavlov.bukkit.container;

import at.pavlov.internal.container.SpawnMaterialHolder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BukkitSpawnMaterialHolder extends SpawnMaterialHolder<BlockData> {
    public BukkitSpawnMaterialHolder(String str) {
        super(str);
    }

    public BukkitSpawnMaterialHolder(BlockData material, int minAmount, int maxAmount) {
        super(material, minAmount, maxAmount);
    }

    @Override
    public void setBlockString(String str) {
        material = Bukkit.createBlockData(str);
    }

    @Override
    public BlockData getDefaultBlock() {
        return Bukkit.createBlockData(Material.AIR);
    }
}
