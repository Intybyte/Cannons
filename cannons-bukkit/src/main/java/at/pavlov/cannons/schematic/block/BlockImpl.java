package at.pavlov.cannons.schematic.block;

import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

public class BlockImpl implements IBlock {
    private final Block block;

    public BlockImpl(Block block) {
        this.block = block;
    }

    @Override
    public int x() {
        return block.getX();
    }

    @Override
    public int y() {
        return block.getY();
    }

    @Override
    public int z() {
        return block.getZ();
    }

    @Override
    public BlockKey key() {
        NamespacedKey key = block.getType().getKey();
        return BlockKey.mc(key.getKey());
    }
}