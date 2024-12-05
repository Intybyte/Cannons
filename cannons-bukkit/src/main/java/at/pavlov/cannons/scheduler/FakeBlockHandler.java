package at.pavlov.cannons.scheduler;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.FakeBlockType;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.container.FakeBlockEntry;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class FakeBlockHandler {
    private final Cannons plugin;

    private final ArrayList<FakeBlockEntry> list = new ArrayList<>();

    private long lastAiming;
    private long lastImpactPredictor;

    @Getter
    private static FakeBlockHandler instance = null;


    private FakeBlockHandler(Cannons plugin) {
        this.plugin = plugin;
    }

    public static void initialize(Cannons plugin) {
        if (instance != null) {
            return;
        }

        instance = new FakeBlockHandler(plugin);
    }

    /**
     * starts the scheduler of the teleporter
     */
    public void setupScheduler() {
        //changing angles for aiming mode
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                removeOldBlocks();
                removeOldBlockType();
            }

        }, 1L, 1L);
    }


    /**
     * removes old blocks form the players vision
     */
    private void removeOldBlocks() {
        Iterator<FakeBlockEntry> iter = list.iterator();
        while (iter.hasNext()) {
            FakeBlockEntry next = iter.next();
            Player player = next.getPlayerBukkit();

            //if player is offline remove this one
            if (player == null) {
                iter.remove();
                continue;
            }

            if (!next.isExpired()) {
                continue;
            }
            //send real block to player
            Location loc = next.getLocation();
            if (loc != null) {
                player.sendBlockChange(loc, loc.getBlock().getBlockData());
                // plugin.logDebug("expired fake block: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + next.getType().toString());
            }
            //remove this entry
            iter.remove();
        }
    }

    /**
     * removes previous entries for this type of fake blocks
     */
    private void removeOldBlockType() {
        Iterator<FakeBlockEntry> iter = list.iterator();
        while (iter.hasNext()) {
            FakeBlockEntry next = iter.next();
            final long start = next.getStartTime();
            final FakeBlockType type = next.getType();
            //if older and if the type matches
            if ((start >= (lastImpactPredictor - 50) || (type != FakeBlockType.IMPACT_PREDICTOR))
                    && (start >= (lastAiming - 50) || (type != FakeBlockType.AIMING))) {
                continue;
            }
            //send real block to player
            Player player = next.getPlayerBukkit();
            Location loc = next.getLocation();
            if (player != null && loc != null) {
                player.sendBlockChange(loc, loc.getBlock().getBlockData());
            }

            //remove this entry
            iter.remove();
            //plugin.logDebug("remove older fake entry: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + next.getType().toString() + " stime " + next.getStartTime());
        }
    }


    /**
     * creates a sphere of fake block and sends it to the given player
     *
     * @param player    the player to be notified
     * @param loc       center of the sphere
     * @param r         radius of the sphere
     * @param blockData material of the fake block
     * @param duration  delay until the block disappears again in s
     */
    public void imitatedSphere(Player player, Location loc, int r, BlockData blockData, FakeBlockType type, double duration) {
        if (loc == null || player == null)
            return;

        LinkedList<BlockState> updates = new LinkedList<>();
        for (int x = -r; x <= r; x++) {
            for (int y = -r; y <= r; y++) {
                for (int z = -r; z <= r; z++) {
                    Location newL = loc.clone().add(x, y, z);
                    if (newL.distance(loc) > r) {
                        continue;
                    }

                    var block = sendBlockChangeToPlayer(player, newL, blockData, type, duration);
                    if (block != null) {
                        updates.add(block);
                    }
                }
            }
        }

        player.sendBlockChanges(updates);
    }

    /**
     * creates a line of blocks at the give location
     *
     * @param loc       starting location of the line
     * @param direction direction of the line
     * @param offset    offset from the starting point
     * @param length    lenght of the line
     * @param player    name of the player
     */
    public void imitateLine(final Player player, Location loc, Vector direction, int offset, int length, BlockData blockData, FakeBlockType type, double duration) {
        if (loc == null || player == null)
            return;

        BlockIterator iter = new BlockIterator(loc.getWorld(), loc.toVector(), direction, offset, length);
        LinkedList<BlockState> updateList = new LinkedList<>();
        while (iter.hasNext()) {
            var block = sendBlockChangeToPlayer(player, iter.next().getLocation(), blockData, type, duration);
            if (block != null)
                updateList.add(block);
        }

        player.sendBlockChanges(updateList);
    }

    /**
     * sends fake block to the given player
     *
     * @param player    player to display the blocks
     * @param loc       location of the block
     * @param blockData type of the block
     * @param duration  how long to remove the block in [s]
     */
    private BlockState sendBlockChangeToPlayer(final Player player, final Location loc, BlockData blockData, FakeBlockType type, double duration) {
        //only show block in air
        if (!loc.getBlock().isEmpty()) {
            return null;
        }

        FakeBlockEntry fakeBlockEntry = new FakeBlockEntry(loc, player, type, (long) (duration * 20.0));

        boolean found = false;
        for (FakeBlockEntry block : list) {
            if (block.equals(fakeBlockEntry)) {
                //renew entry
                //plugin.logDebug("renew block at: " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ", " + type.toString());
                block.setStartTime(System.currentTimeMillis());
                found = true;
                //there is only one block here
                break;
            }
        }

        if (!found) {
            //player.sendBlockChange(loc, blockData);
            list.add(fakeBlockEntry);
        }


        if (type == FakeBlockType.IMPACT_PREDICTOR)
            lastImpactPredictor = System.currentTimeMillis();
        if (type == FakeBlockType.AIMING)
            lastAiming = System.currentTimeMillis();

        return blockData.createBlockState().copy(loc);
    }

    /**
     * returns true if the distance is in between the min and max limits of the imitate block distance
     *
     * @param player player the check
     * @param loc    location of the block
     * @return true if the distance is in the limits
     */
    public boolean isBetweenLimits(Player player, Location loc) {
        if (player == null || loc == null)
            return false;

        double dist = player.getLocation().distance(loc);
        Config config = plugin.getMyConfig();
        return config.getImitatedBlockMinimumDistance() < dist && dist < config.getImitatedBlockMaximumDistance();
    }

    /**
     * returns true if the distance is below max limit of the imitate block distance
     *
     * @param player player the check
     * @param loc    location of the block
     * @return true if the distance is smaller than upper limit
     */
    public boolean belowMaxLimit(Player player, Location loc) {
        if (player == null || loc == null)
            return false;

        double dist = player.getLocation().distance(loc);
        return dist < plugin.getMyConfig().getImitatedBlockMaximumDistance();
    }

}