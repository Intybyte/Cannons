package at.pavlov.cannons.listener;

import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.multiversion.EventResolver;
import at.pavlov.cannons.utils.CannonSelector;
import at.pavlov.cannons.utils.EventUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.Iterator;
import java.util.List;

public class BlockListener implements Listener {
    private final Cannons plugin;
    private final CannonManager cannonManager;

    public BlockListener(Cannons plugin) {
        this.plugin = plugin;
        this.cannonManager = CannonManager.getInstance();
    }

    @EventHandler
    public void blockExplodeEvent(BlockExplodeEvent event) {
        if (plugin.getMyConfig().isRelayExplosionEvent()) {
            EntityExplodeEvent explodeEvent = EventResolver.getEntityExplodeEvent(null, event.getBlock().getLocation(), event.blockList(), event.getYield());
            Bukkit.getServer().getPluginManager().callEvent(explodeEvent);
            event.setCancelled(explodeEvent.isCancelled());
        }

        //cannons event - remove unbreakable blocks like bedrock
        //this will also affect other plugins which spawn bukkit explosions
        List<Block> blocks = event.blockList();
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            for (BlockData unbreakableBlock : plugin.getMyConfig().getUnbreakableBlocks()) {
                if (unbreakableBlock.matches(block.getBlockData())) {
                    blocks.remove(i--);
                }
            }
        }

        //search for destroyed cannons
        event.blockList().removeIf(it -> it.getType().isAir());
        EventUtils.handleExplosion(event.blockList());
    }

    /**
     * Water will not destroy button and torches
     *
     * @param event
     */
    @EventHandler
    public void blockFromTo(BlockFromToEvent event) {
        Block block = event.getToBlock();
        Cannon cannon = cannonManager.getCannon(block.getLocation(), null);
        if (cannon == null) {
            return;
        }
        
        if (cannon.isCannonBlock(block)) {
            event.setCancelled(true);
        }
    }

    /**
     * prevent fire on cannons
     *
     * @param event
     */
    @EventHandler
    public void blockSpread(BlockSpreadEvent event) {
        Block block = event.getBlock().getRelative(BlockFace.DOWN);
        Cannon cannon = cannonManager.getCannon(block.getLocation(), null);

        if (cannon != null && cannon.isCannonBlock(block)) {
            event.setCancelled(true);
        }
    }

    /**
     * retraction pistons will trigger this event. If the pulled block is part of a cannon, it is canceled
     *
     * @param event - BlockPistonRetractEvent
     */
    @EventHandler
    public void blockPistonRetract(BlockPistonRetractEvent event) {
        // when piston is sticky and has a cannon block attached delete the
        // cannon
        if (!event.isSticky()) {
            return;
        }

        Location loc = event.getBlock().getRelative(event.getDirection(), 2).getLocation();
        Cannon cannon = cannonManager.getCannon(loc, null);
        if (cannon != null) {
            event.setCancelled(true);
        }
    }

    /**
     * pushing pistons will trigger this event. If the pused block is part of a cannon, it is canceled
     *
     * @param event - BlockPistonExtendEvent
     */
    @EventHandler
    public void blockPistonExtend(BlockPistonExtendEvent event) {
        // when the moved block is a cannonblock
        for (Iterator<Block> iter = event.getBlocks().iterator(); iter.hasNext(); ) {
            // if moved block is cannonBlock delete cannon
            Cannon cannon = cannonManager.getCannon(iter.next().getLocation(), null);
            if (cannon != null) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * if the block catches fire this event is triggered. Cannons can't burn.
     *
     * @param event - BlockBurnEvent
     */
    @EventHandler
    public void blockBurn(BlockBurnEvent event) {
        // the cannon will not burn down
        if (cannonManager.getCannon(event.getBlock().getLocation(), null) != null) {
            event.setCancelled(true);
        }
    }

    /**
     * if one block of the cannon is destroyed, it is removed from the list of cannons
     *
     * @param event - BlockBreakEvent
     */
    @EventHandler
    public void blockBreak(BlockBreakEvent event) {

        Block block = event.getBlock();
        Location location = block.getLocation();
        Cannon cannon = cannonManager.getCannon(block.getLocation(), null);


        if (cannon != null) {
            //breaking is only allowed when the barrel is broken - minor stuff as buttons are canceled
            //you can't break your own cannon in aiming mode
            //breaking cannon while player is in selection (command) mode is not allowed
            Cannon aimingCannon = null;
            if (Aiming.getInstance().isInAimingMode(event.getPlayer().getUniqueId()))
                aimingCannon = Aiming.getInstance().getCannonInAimingMode(event.getPlayer());

            if (cannon.isDestructibleBlock(location) && (!cannon.equals(aimingCannon)) && !CannonSelector.getInstance().isSelectingMode(event.getPlayer())) {
                cannonManager.removeCannon(cannon, false, true, BreakCause.PlayerBreak);
                plugin.logDebug("cannon broken:  " + cannon.isDestructibleBlock(location));
            } else {
                event.setCancelled(true);
                plugin.logDebug("cancelled cannon destruction: " + cannon.isDestructibleBlock(location));
            }
        }

        //if the the last block on a cannon is broken and signs are required
        if (block.getBlockData() instanceof WallSign sign) {
            cannon = cannonManager.getCannon(block.getRelative(sign.getFacing().getOppositeFace()).getLocation(), null);
            plugin.logDebug("cancelled cannon sign  " + block.getRelative(sign.getFacing().getOppositeFace()));
            if (cannon != null && cannon.getCannonDesign().isSignRequired() && cannon.getNumberCannonSigns() <= 1) {
                plugin.logDebug("cancelled cannon sign destruction");
                event.setCancelled(true);
            }
        }
    }
}
