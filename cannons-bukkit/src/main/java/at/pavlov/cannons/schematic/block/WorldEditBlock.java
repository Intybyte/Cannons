package at.pavlov.cannons.schematic.block;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import lombok.AllArgsConstructor;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;

@AllArgsConstructor
public class WorldEditBlock implements IBlock {
    private final BlockState state;
    private final BlockVector3 pos;

    @Override
    public int x() {
        return pos.getBlockX();
    }

    @Override
    public int y() {
        return pos.getBlockY();
    }

    @Override
    public int z() {
        return pos.getBlockZ();
    }

    @Override
    public BlockKey key() {
        String id = state.getBlockType().getId();
        String[] splitted = id.split(":");
        return new BlockKey(splitted[0], splitted[1]);
    }
}
