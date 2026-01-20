package at.pavlov.cannons.schematic.block;

import lombok.Getter;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;

@Getter
public class BlockImpl implements IBlock {
    private final Block block;
    private final BlockKey key;

    public BlockImpl(Block block) {
        this.block = block;
        NamespacedKey key = block.getType().getKey();
        this.key = BlockKey.mc(key.getKey());
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
        return this.key;
    }
}