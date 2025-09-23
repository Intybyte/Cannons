package at.pavlov.cannons.schematic.namespace;

import at.pavlov.cannons.Cannons;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockPlaceHandler;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.file.block.FileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// doesn't support handlers as we have no acting player
public class SlimefunNamespaceHandler implements NamespaceHandler {
    @Override
    public void place(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        String id = iBlock.key().key();
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());
        Block block = location.getBlock();

        SlimefunItem sfItem = SlimefunItem.getById(id.toUpperCase());
        if (sfItem == null) {
            return;
        }

        Material material = sfItem.getItem().getType();
        if (!material.isBlock()) {
            return;
        }

        if (Slimefun.getTickerTask().isDeletedSoon(location)) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    if (Slimefun.getTickerTask().isDeletedSoon(location)) {
                        return;
                    }

                    place(iBlock, uuid);
                    cancel();
                }
            }.runTaskTimer(Cannons.getPlugin(), 1L, 4L);
            return;
        }

        block.setType(material);
        if (Slimefun.getBlockDataService().isTileEntity(block.getType())) {
            Slimefun.getBlockDataService().setBlockData(block, sfItem.getId());
        }

        BlockStorage.addBlockInfo(block, "id", sfItem.getId(), true);
    }

    @Override
    public IBlock get(int x, int y, int z, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        SlimefunItem item = BlockStorage.check(new Location(world, x, y, z));
        if (item == null) {
            return null;
        }

        String id = item.getId().toLowerCase();
        return new FileBlock(x, y, z, new BlockKey("slimefun", id));
    }

    @Override
    public void destroy(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());

        SlimefunItem sfItem = BlockStorage.check(location);
        if (sfItem == null) {
            return; // probably old data from a removed addon?
        }

        BlockMenu inventory = BlockStorage.getInventory(location);
        if (inventory != null) {
            for (HumanEntity human : new ArrayList<>(inventory.toInventory().getViewers())) {
                human.closeInventory();
            }
        }

        BlockStorage.clearBlockInfo(location);
        location.getBlock().setType(Material.AIR);
    }

    @Override
    public void breakNaturally(IBlock iBlock, UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        Location location = new Location(world, iBlock.x(), iBlock.y(), iBlock.z());

        SlimefunItem sfItem = BlockStorage.check(location);
        if (sfItem == null) {
            return; // probably old data from a removed addon?
        }

        List<ItemStack> drops = new ArrayList<>(sfItem.getDrops());
        BlockMenu inventory = BlockStorage.getInventory(location);
        if (inventory != null) {
            for (HumanEntity human : new ArrayList<>(inventory.toInventory().getViewers())) {
                human.closeInventory();
            }
        }

        BlockStorage.clearBlockInfo(location);

        for (ItemStack drop : drops) {
            if (drop != null && drop.getType() != Material.AIR) {
                world.dropItemNaturally(location, drop);
            }
        }

        location.getBlock().setType(Material.AIR);
    }

    @Override
    public BlockKey toMaterial(BlockKey blockKey) {
        SlimefunItem sfItem = SlimefunItem.getById(blockKey.key().toUpperCase());
        if (sfItem == null) {
            return BlockKey.mc("air");
        }

        String materialType = sfItem.getItem().getType().getKey().getKey();
        return BlockKey.mc(materialType);
    }
}
