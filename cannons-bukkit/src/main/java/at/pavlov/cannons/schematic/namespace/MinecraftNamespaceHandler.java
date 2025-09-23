package at.pavlov.cannons.schematic.namespace;


import at.pavlov.cannons.schematic.block.BlockImpl;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.UUID;

public class MinecraftNamespaceHandler implements NamespaceHandler {
    @Override
    public void place(IBlock iBlock, UUID uuid) {
        NamespacedKey key = new NamespacedKey("minecraft", iBlock.key().key());
        Material mat = Registry.MATERIAL.get(key);
        Bukkit.getWorld(uuid).getBlockAt(
            iBlock.x(),
            iBlock.y(),
            iBlock.z()
        ).setType(mat);
    }

    @Override
    public IBlock get(int x, int y, int z, UUID uuid) {
        return new BlockImpl(
            Bukkit.getWorld(uuid).getBlockAt(x, y, z)
        );
    }

    @Override
    public void destroy(IBlock iBlock, UUID uuid) {
        Bukkit.getWorld(uuid).getBlockAt(
            iBlock.x(),
            iBlock.y(),
            iBlock.z()
        ).setType(Material.AIR);
    }

    @Override
    public void breakNaturally(IBlock iBlock, UUID uuid) {
        Bukkit.getWorld(uuid).getBlockAt(
            iBlock.x(),
            iBlock.y(),
            iBlock.z()
        ).breakNaturally();
    }

    // should be unused
    @Override
    public BlockKey toMaterial(BlockKey blockKey) {
        return blockKey;
    }
}
