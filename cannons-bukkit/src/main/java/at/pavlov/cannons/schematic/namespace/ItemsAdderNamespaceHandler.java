package at.pavlov.cannons.schematic.namespace;

import at.pavlov.cannons.schematic.RetryUntil;
import at.pavlov.cannons.utils.SchemLibUtils;
import dev.lone.itemsadder.api.CustomBlock;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.file.block.FileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;

public class ItemsAdderNamespaceHandler implements NamespaceHandler {

    @Override
    public void place(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());

        String realKey = iBlock.key().key().replace('$', ':');
        CustomBlock.place(realKey, location);
    }

    @Override
    public IBlock get(int x, int y, int z, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, x, y, z);

        Block bBlock = location.getBlock();
        CustomBlock iaBlock = CustomBlock.byAlreadyPlaced(bBlock);
        if (iaBlock == null) {
            return null;
        }

        // war crimes go brrrrrrrrrr
        String replaced = iaBlock.getNamespacedID().replace(':', '$');
        return new FileBlock(x, y, z, new BlockKey("itemsadder", replaced));
    }

    @Override
    public void destroy(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());

        if (!CustomBlock.remove(location)) {
            new RetryUntil(
                () -> !CustomBlock.remove(location)
            ).start();
        }
    }

    @Override
    public void breakNaturally(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());

        Block bBlock = location.getBlock();
        CustomBlock iaBlock = CustomBlock.byAlreadyPlaced(bBlock);
        iaBlock.drop(location);

        destroy(iBlock, uuid);
    }

    @Override
    public BlockKey toMaterial(BlockKey blockKey) {
        String realKey = blockKey.key().replace('$', ':');
        Material material = CustomBlock.getBaseBlockData(realKey).getMaterial();
        return SchemLibUtils.materialKey(material);
    }
}
