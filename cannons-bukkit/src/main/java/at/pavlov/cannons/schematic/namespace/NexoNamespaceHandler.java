package at.pavlov.cannons.schematic.namespace;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.file.block.FileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.UUID;

public class NexoNamespaceHandler implements NamespaceHandler {
    @Override
    public void place(IBlock iBlock, UUID world) {
        String item_id = iBlock.key().key().replace('$', ':');
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());
        NexoBlocks.place(item_id, loc);
    }

    @Override
    public IBlock get(int x, int y, int z, UUID world) {
        Block block = new Location(Bukkit.getWorld(world), x, y, z).getBlock();

        CustomBlockMechanic cbm = NexoBlocks.customBlockMechanic(block);
        if (cbm == null) return null;

        String item_id = cbm.getItemID().replace(':', '$');

        return new FileBlock(x, y, z, new BlockKey("nexo", item_id));
    }

    @Override
    public void destroy(IBlock iBlock, UUID world) {

        // 0 idea how to use this, but maybe this is what I am looking for
        //CustomBlockRegistry.INSTANCE.getMechanic("ee").getType().removeWorldEdit();
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());
        NexoBlocks.remove(loc);
    }

    @Override
    public void breakNaturally(IBlock iBlock, UUID world) {
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());
        NexoBlocks.remove(loc, null, true);
    }

    @Override
    public BlockKey toMaterial(BlockKey blockKey) {
        String item_id = blockKey.key().replace('$', ':');
        BlockData data = NexoBlocks.blockData(item_id);
        NamespacedKey key = data.getMaterial().getKey();

        return BlockKey.mc(key.getKey());
    }
}
