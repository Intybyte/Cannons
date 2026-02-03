package at.pavlov.cannons.schematic.namespace;

import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.utils.CannonSelector;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.events.furniture.NexoFurnitureBreakEvent;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.block.ICoord;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.file.block.FileBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class NexoNamespaceHandler implements NamespaceHandler, Listener, Initialize {
    @Override
    public void init() {
        Cannons pl = Cannons.getPlugin();
        // furnitures aren't handled by block break
        Bukkit.getPluginManager().registerEvents(this, pl);
        // nexo block resolution requires some time first, so rerun load
        Bukkit.getScheduler().runTaskLater(pl, DesignStorage.getInstance()::loadCannonDesigns, 5L);
    }

    @Override
    public void place(IBlock iBlock, UUID world) {
        String item_id = iBlock.key().key().replace('$', ':');
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());

        if (NexoBlocks.isCustomBlock(item_id)) NexoBlocks.place(item_id, loc);

        FurnitureMechanic fm = getOneBlockFurniture(item_id);
        if (fm == null) return;
        NexoFurniture.place(item_id, loc, 0f, BlockFace.NORTH);
    }

    @Override
    public IBlock get(int x, int y, int z, UUID world) {
        Block block = new Location(Bukkit.getWorld(world), x, y, z).getBlock();

        CustomBlockMechanic cbm = NexoBlocks.customBlockMechanic(block);
        if (cbm != null) {
            String item_id = cbm.getItemID().replace(':', '$');
            return new FileBlock(x, y, z, new BlockKey("nexo", item_id));
        }

        FurnitureMechanic fm = NexoFurniture.furnitureMechanic(block);
        if (fm != null && fm.getHitbox().getBarriers().size() == 1) {
            String item_id = fm.getItemID().replace(':', '$');
            return new FileBlock(x, y, z, new BlockKey("nexo", item_id));
        }

        return null;

    }

    @Override
    public void destroy(IBlock iBlock, UUID world) {

        // 0 idea how to use this, but maybe this is what I am looking for
        //CustomBlockRegistry.INSTANCE.getMechanic("ee").getType().removeWorldEdit();
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());
        Block block = loc.getBlock();
        if (NexoBlocks.isCustomBlock(block)) NexoBlocks.remove(loc);
        if (NexoFurniture.isFurniture(loc)) NexoFurniture.remove(loc);
    }

    @Override
    public void breakNaturally(IBlock iBlock, UUID world) {
        Location loc = new Location(Bukkit.getWorld(world), iBlock.x(), iBlock.y(), iBlock.z());
        Block block = loc.getBlock();
        if (NexoBlocks.isCustomBlock(block)) NexoBlocks.remove(loc, null, true);
        if (NexoFurniture.isFurniture(loc)) NexoFurniture.remove(loc);
    }

    @Override
    public void move(ICoord from, ICoord to, BlockKey blockKey) {

    }

    private static final BlockKey BARRIER = BlockKey.mc("air");
    @Override
    public BlockKey toMaterial(BlockKey blockKey) {
        String item_id = blockKey.key().replace('$', ':');
        BlockData data = NexoBlocks.blockData(item_id);
        if (data != null) {
            NamespacedKey key = data.getMaterial().getKey();
            return BlockKey.mc(key.getKey());
        }

        if (NexoFurniture.isFurniture(item_id)) return BARRIER;

        throw new UnsupportedOperationException();
    }

    private FurnitureMechanic getOneBlockFurniture(String id) {
        FurnitureMechanic fm = NexoFurniture.furnitureMechanic(id);
        if (fm == null) return null;
        if (fm.getHitbox().getBarriers().size() != 1) return null;

        return fm;
    }

    @EventHandler(ignoreCancelled = true)
    private void onFurnitureBreak(NexoFurnitureBreakEvent event) {
        if (event.getMechanic().getHitbox().getBarriers().size() != 1) return;

        CannonManager cannonManager = CannonManager.getInstance();
        Cannons plugin = Cannons.getPlugin();

        Location location = event.getBaseEntity().getLocation().getBlock().getLocation();
        Cannon cannon = cannonManager.getCannonFromStorage(location);

        if (cannon == null) {
            return;
        }

        Cannon aimingCannon = null;
        Player player = event.getPlayer();
        if (Aiming.getInstance().isInAimingMode(player.getUniqueId()))
            aimingCannon = Aiming.getInstance().getCannonInAimingMode(player);

        if (!cannon.equals(aimingCannon) && !CannonSelector.getInstance().isSelectingMode(player)) {
            cannonManager.removeCannon(cannon, false, true, BreakCause.PlayerBreak);
            plugin.logDebug("cannon broken:  " + location);
        } else {
            event.setCancelled(true);
            plugin.logDebug("cancelled cannon destruction: " + location);
        }
    }
}
