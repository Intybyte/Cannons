package at.pavlov.cannons.cannon;

import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.config.UserMessages;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SimpleBlock;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.cannons.dao.LoadWhitelistTask;
import at.pavlov.cannons.event.CannonAfterCreateEvent;
import at.pavlov.cannons.event.CannonBeforeCreateEvent;
import at.pavlov.cannons.event.CannonRenameEvent;
import at.pavlov.cannons.dao.wrappers.RemoveTaskWrapper;
import at.pavlov.cannons.exchange.BExchanger;
import at.pavlov.cannons.exchange.EmptyExchanger;
import at.pavlov.cannons.schematic.world.SchematicWorldProcessorImpl;
import at.pavlov.cannons.utils.SoundUtils;
import com.google.common.base.Preconditions;
import lombok.Getter;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.schematic.OffsetSchematic;
import me.vaan.schematiclib.base.schematic.OffsetSchematicImpl;
import me.vaan.schematiclib.base.schematic.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class CannonManager {
    private static final ConcurrentHashMap<UUID, Cannon> cannonList = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, UUID> cannonNameMap = new ConcurrentHashMap<>();

    private final Cannons plugin;
    private final UserMessages userMessages;
    private final Config config;
    private static final BlockFace[] blockFaces = { BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST };

    @Getter
    private static CannonManager instance = null;

    private CannonManager(Cannons cannons) {
        this.userMessages = UserMessages.getInstance();
        this.config = Config.getInstance();
        this.plugin = cannons;
    }

    public static void initialize(Cannons cannons) {
        if (instance != null) {
            return;
        }

        instance = new CannonManager(cannons);
    }

    /**
     * removes a cannons from the list that are not valid
     *
     * @param cause the reason why the cannon is removed
     */
    public void removeInvalidCannons(BreakCause cause) {
        Iterator<Cannon> iter = cannonList.values().iterator();

        while (iter.hasNext()) {
            Cannon next = iter.next();
            if (!next.isValid()) {
                removeCannon(next, false, false, cause, false, false);
                iter.remove();
            }
        }
    }

    /**
     * deconstructs a cannon without the risk of explosion
     *
     * @param cannon cannon to remove
     */
    public void dismantleCannon(Cannon cannon, Player player) {
        if (cannon == null)
            return;
        if (player == null) {
            removeCannon(cannon, true, false, BreakCause.Dismantling);
            return;
        }
        // admins can dismantle all cannons
        if (player.hasPermission("cannons.admin.dismantle")) {
            removeCannon(cannon, true, false, BreakCause.Dismantling);
            return;
        }

        if (player.hasPermission(cannon.getCannonDesign().getPermissionDismantle())) {
            //only the owner of the cannon can dismantle a cannon
            if (cannon.getOwner() != null && cannon.getOwner().equals(player.getUniqueId()))
                removeCannon(cannon, true, false, BreakCause.Dismantling);
            else
                userMessages.sendMessage(MessageEnum.ErrorDismantlingNotOwner, player, cannon);
            return;
        }

        userMessages.sendMessage(MessageEnum.PermissionErrorDismantle, player, cannon);
    }

    /**
     * removes a cannon from the list
     *
     * @param loc         location of the cannon
     * @param breakCannon all cannon blocks will drop
     * @param canExplode  if the cannon can explode when loaded with gunpowder
     * @param cause       the reason way the cannon was broken
     */
    public void removeCannon(Location loc, boolean breakCannon, boolean canExplode, BreakCause cause) {
        Cannon cannon = getCannon(loc, null);
        removeCannon(cannon, breakCannon, canExplode, cause);
    }

    /**
     * removes a cannon from the list
     *
     * @param cannon      cannon to remove
     * @param breakCannon all cannon blocks will drop
     * @param canExplode  if the cannon can explode when loaded with gunpowder
     * @param cause       the reason way the cannon was broken
     */
    public void removeCannon(Cannon cannon, boolean breakCannon, boolean canExplode, BreakCause cause) {
        removeCannon(cannon, breakCannon, canExplode, cause, true, true);
    }

    /**
     * removes a cannon from the list
     *
     * @param uid         UID of the cannon
     * @param breakCannon all cannon blocks will drop
     * @param canExplode  if the cannon can explode when loaded with gunpowder
     * @param cause       the reason way the cannon was broken
     */
    public void removeCannon(UUID uid, boolean breakCannon, boolean canExplode, BreakCause cause) {
        removeCannon(uid, breakCannon, canExplode, cause, true);
    }

    /**
     * removes a cannon from the list
     *
     * @param uid         UID of the cannon
     * @param breakCannon all cannon blocks will drop
     * @param canExplode  if the cannon can explode when loaded with gunpowder
     * @param cause       the reason way the cannon was broken
     * @param removeEntry should the cannon be removed from the list
     */
    public void removeCannon(UUID uid, boolean breakCannon, boolean canExplode, BreakCause cause, boolean removeEntry) {
        Cannon cannon = cannonList.get(uid);
        removeCannon(cannon, breakCannon, canExplode, cause, removeEntry, true);
    }


    /**
     * removes a cannon from the list
     *
     * @param cannon        cannon to remove
     * @param breakCannon   all cannon blocks will drop
     * @param canExplode    if the cannon can explode when loaded with gunpowder
     * @param cause         the reason way the cannon was broken
     * @param ignoreInvalid if true invalid cannons will be skipped and not removed
     */
    public void removeCannon(Cannon cannon, boolean breakCannon, boolean canExplode, BreakCause cause, boolean removeEntry, boolean ignoreInvalid) {
        //ignore invalid cannons
        if (cannon == null || (!cannon.isValid() && ignoreInvalid))
            return;

        long delay = 0;
        if (cause == BreakCause.Dismantling || cause == BreakCause.Other) {
            plugin.logDebug("Dismantling," + cannon.getCannonDesign().getSoundDismantle().toString());
            SoundUtils.playSound(cannon.getRandomBarrelBlock(), cannon.getCannonDesign().getSoundDismantle());
            delay = (long) (cannon.getCannonDesign().getDismantlingDelay() * 20.0);
        } else {
            SoundUtils.playSound(cannon.getRandomBarrelBlock(), cannon.getCannonDesign().getSoundDestroy());
        }

        //delay the remove task, so it fits to the sound
        RemoveTaskWrapper task = new RemoveTaskWrapper(cannon, breakCannon, canExplode, cause, removeEntry, ignoreInvalid);
        AsyncTaskManager.get().scheduler.runTaskLater( () -> {
            Cannon cannon1 = task.cannon();
            BreakCause cause1 = task.cause();
            UUID owner = cannon1.getOwner();

            // send message to the owner
            Player player = null;
            if (owner != null) {
                player = Bukkit.getPlayer(owner);

                OfflinePlayer offplayer = Bukkit.getOfflinePlayer(owner);
                // return message
                if (offplayer.hasPlayedBefore() && cannon1.isPaid()) {
                    BExchanger funds = switch (cause1) {
                        case Other, Dismantling -> cannon1.getCannonDesign().getEconomyDismantlingRefund();
                        default -> cannon1.getCannonDesign().getEconomyDestructionRefund();
                    };

                    funds.execute(offplayer, cannon);
                }
            }

            MessageEnum message = cannon1.destroyCannon(task.breakCannon(), task.canExplode(), cause1);
            if (player != null)
                userMessages.sendMessage(message, player, cannon1);

            //remove from database
            plugin.getPersistenceDatabase().deleteCannon(cannon1.getUID());
            //remove cannon name
            cannonNameMap.remove(cannon1.getCannonName());
            //remove sentry
            if (cannon1.getCannonDesign().isSentry())
                Aiming.getInstance().removeSentryCannon(cannon1.getUID());
            //remove all entries for this cannon in the aiming class
            Aiming.getInstance().removeCannon(cannon1);

            //remove entry
            if (task.removeEntry())
                cannonList.remove(cannon1.getUID());

        }, delay);
    }


    /**
     * Checks if the name of a cannon is unique
     *
     * @param name name of the cannon
     * @return true if the name is unique
     */
    public static boolean isCannonNameUnique(String name) {
        if (name == null)
            return false;

        // try to find this in the map
        //there is no such cannon name
        //there is such a cannon name
        return cannonNameMap.get(name) == null;
    }

    /**
     * generates a new unique cannon name
     *
     * @return name string for the new cannon
     */
    public String newCannonName(Cannon cannon) {
        //check if this cannon has a owner
        if (cannon.getOwner() == null)
            return "missing Owner";

        String name;
        CannonDesign design = cannon.getCannonDesign();
        if (design != null)
            name = design.getDesignName();
        else
            name = "cannon";


        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            String cannonName = name + " " + i;

            if (isCannonNameUnique(cannonName)) {
                return cannonName;
            }
        }
        return "no unique name";
    }

    public MessageEnum renameCannon(@NotNull Player player, @NotNull Cannon cannon, String newCannonName) {
        Preconditions.checkNotNull(player, "player must not be null");
        Preconditions.checkNotNull(cannon, "cannon must not be null");

        //check some permissions
        if (cannon.getOwner() != null && !player.getUniqueId().equals(cannon.getOwner()))
            return MessageEnum.ErrorNotTheOwner;
        if (!player.hasPermission(cannon.getCannonDesign().getPermissionRename()))
            return MessageEnum.PermissionErrorRename;
        if (newCannonName == null || !isCannonNameUnique(newCannonName))
            return MessageEnum.CannonRenameFail;

        //put the new name
        CannonRenameEvent event = new CannonRenameEvent(player, cannon, newCannonName);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
            return MessageEnum.ErrorGeneric;

        cannon.setCannonName(newCannonName);

        return MessageEnum.CannonRenameSuccess;

    }

    /**
     * adds a new cannon to the list of cannons
     *
     * @param cannon         create this cannon
     * @param saveToDatabase if the cannon will be saved to the database after loading
     */
    public void createCannon(Cannon cannon, boolean saveToDatabase) {
        //the owner can't be null
        if (cannon.getOwner() == null) {
            plugin.logInfo("can't save a cannon when the owner is null");
            return;
        }

        //ignore paid if there is no economy
        if (cannon.getCannonDesign().getEconomyBuildingCost() instanceof EmptyExchanger)
            cannon.setPaid(true);

        //add owner to whitelist for sentry
        if (cannon.getCannonDesign().isSentry())
            cannon.addWhitelistPlayer(cannon.getOwner());

        //if the cannonName is empty make a new one
        if (cannon.getCannonName() == null || cannon.getCannonName().isEmpty())
            cannon.setCannonName(newCannonName(cannon));

        // add cannon to the list
        cannonList.put(cannon.getUID(), cannon);
        //add cannon name to the list
        cannonNameMap.put(cannon.getCannonName(), cannon.getUID());
        if (cannon.getCannonDesign().isSentry())
            Aiming.getInstance().addSentryCannon(cannon.getUID());

        if (saveToDatabase) {
            plugin.getPersistenceDatabase().saveCannon(cannon);
        } else {
            cannon.setUpdated(false);
            cannon.setWhitelistUpdated(false);
        }
        plugin.logDebug("added cannon " + cannon.getCannonName());

        LoadWhitelistTask loadWhitelistTask = new LoadWhitelistTask(cannon.getUID());
        loadWhitelistTask.runTaskAsynchronously();
    }

    /**
     * returns all known cannons in a sphere around the given location
     *
     * @param center       - center of the box
     * @param sphereRadius - radius of the sphere in blocks
     * @return - list of all cannons in this sphere
     */
    public static HashSet<Cannon> getCannonsInSphere(Location center, double sphereRadius) {
        HashSet<Cannon> newCannonList = new HashSet<>();

        for (Cannon cannon : getCannonList().values()) {
            if (!cannon.getWorld().equals(center.getWorld().getUID())) {
                continue;
            }

            Location newLoc = cannon.getCannonDesign().getBarrelBlocks(cannon).get(0);
            if (newLoc.distanceSquared(center) < sphereRadius * sphereRadius)
                newCannonList.add(cannon);
        }

        return newCannonList;
    }

    /**
     * returns all known cannons in a box around the given location
     *
     * @param center  - center of the box
     * @param lengthX - box length in X
     * @param lengthY - box length in Y
     * @param lengthZ - box length in Z
     * @return - list of all cannons in this sphere
     */
    public static HashSet<Cannon> getCannonsInBox(Location center, double lengthX, double lengthY, double lengthZ) {
        HashSet<Cannon> newCannonList = new HashSet<>();

        for (Cannon cannon : getCannonList().values()) {
            if (!cannon.getWorld().equals(center.getWorld().getUID())) {
                continue;
            }

            Location newLoc = cannon.getCannonDesign().getBarrelBlocks(cannon).get(0);
            Vector box = newLoc.subtract(center).toVector();
            if (Math.abs(box.getX()) < lengthX / 2 && Math.abs(box.getY()) < lengthY / 2 && Math.abs(box.getZ()) < lengthZ / 2)
                newCannonList.add(cannon);
        }
        return newCannonList;
    }

    //always call this in a thread if possible
    public List<Cannon> fetchCannonInBox(Location center, UUID owner, int range) {
        LinkedList<Cannon> list = new LinkedList<>();

        for (int x = range; x >= -range; x--) {
            for (int y = range; y >= -range; y--) {
                for (int z = range; z >= -range; z--) {
                    list.add(getCannon(center.clone().add(x, y, z), owner));
                }
            }
        }

        return list;
    }

    public void dismantleCannonsInBox(Player player, Location center, int size) {
        var taskManager = AsyncTaskManager.get();
        taskManager.scheduler.runTask(center, () -> {
            var cannonHashSet = getCannonsInBox(center, size, size, size);
            for (Cannon cannon : cannonHashSet) {
                taskManager.scheduler.runTask(cannon.getLocation(), () -> {
                    dismantleCannon(cannon, player);
                });
            }
        });
    }


    /**
     * returns all cannons for a list of locations
     *
     * @param locations - a list of location to search for cannons
     * @return - list of all cannons in this sphere
     */
    public static HashSet<Cannon> getCannonsByLocations(List<Location> locations) {
        HashSet<Cannon> newCannonList = new HashSet<>();
        for (Cannon cannon : getCannonList().values()) {
            for (Location loc : locations) {
                if (cannon.isCannonBlock(loc.getBlock()))
                    newCannonList.add(cannon);
            }
        }
        return newCannonList;
    }

    /**
     * returns all cannons for a list of locations - this will update all locations
     *
     * @param locations - a list of location to search for cannons
     * @param player    - player which operates the cannon
     * @param silent    - no messages will be displayed if silent is true
     * @return - list of all cannons in this sphere
     */
    public HashSet<Cannon> getCannons(List<Location> locations, UUID player, boolean silent) {
        HashSet<Cannon> newCannonList = new HashSet<>();
        for (Location loc : locations) {
            Cannon newCannon = getCannon(loc, player, silent);
            if (newCannon != null) {
                newCannonList.add(newCannon);
            }
        }

        return newCannonList;
    }

    /**
     * get cannon by cannonName and Owner - used for Signs
     *
     * @param cannonName name of the cannon
     * @return the cannon with this name
     */
    public static Cannon getCannon(String cannonName) {
        if (cannonName == null) return null;

        UUID uid = cannonNameMap.get(cannonName);
        if (uid != null)
            return cannonList.get(uid);

        //cannon not found
        return null;
    }

    /**
     * Searches the storage if there is already a cannonblock on this location
     * and returns the cannon
     *
     * @param loc location of one cannon block
     * @return the cannon at this location
     */
    public Cannon getCannonFromStorage(Location loc) {
        for (Cannon cannon : cannonList.values()) {
            //To make code faster on servers with a lot of cannons we check the distance squared
            if (loc.toVector().distanceSquared(cannon.getOffset()) <= 1024 && cannon.isCannonBlock(loc.getBlock())) {
                return cannon;
            }
        }
        return null;
    }

    /**
     * searches for a cannon and creates a new entry if it does not exist
     *
     * @param cannonBlock - one block of the cannon
     * @param owner       - the owner of the cannon (important for message notification). Can't be null if a new cannon is created
     * @return the cannon at this location
     */
    public Cannon getCannon(Location cannonBlock, UUID owner) {
        return getCannon(cannonBlock, owner, false);
    }

    /**
     * searches for a cannon and creates a new entry if it does not exist
     *
     * @param cannonBlock - one block of the cannon
     * @param owner       - the owner of the cannon (important for message notification). Can't be null
     * @param silent      - if true the message and sounds won't be fire to the player
     * @return the cannon at this location
     */
    public Cannon getCannon(Location cannonBlock, UUID owner, boolean silent) {
        // is this block material used for a cannon design
        if (!isValidCannonBlock(cannonBlock.getBlock()))
            return null;

        long startTime = System.nanoTime();

        //check if there is a cannon at this location
        Cannon cannon = checkCannon(cannonBlock, owner);

        //if there is no cannon, exit
        if (cannon == null)
            return null;

        // this cannon has no sign, so look in the database if there is something
        Cannon storageCannon = getCannonFromStorage(cannonBlock);
        if (storageCannon != null) {
            //try to find something in the storage
            plugin.logDebug("cannon found in storage");
            cannon = storageCannon;

            plugin.logDebug("Time to find cannon: " + new DecimalFormat("0.00").format((System.nanoTime() - startTime) / 1000000.0) + "ms. Cannon name: " + cannon.getCannonName());
            return cannon;
        } //nothing in the storage, so we make a new entry

        //search for a player, because owner == null is not valid
        if (owner == null)
            return null;
        Player player = Bukkit.getPlayer(owner);

        //can this player can build one more cannon
        MessageEnum message = canBuildCannon(cannon, owner);

        //check the permissions for redstone
        if (message == null || message == MessageEnum.CannonCreated)
            message = cannon.checkRedstonePermission(owner);

        //if a sign is required to operate the cannon, there must be at least one sign
        if (message == MessageEnum.CannonCreated && (cannon.getCannonDesign().isSignRequired() && !cannon.hasCannonSign()))
            message = MessageEnum.ErrorMissingSign;

        assert player != null;

        if(!fireCannonBeforeCreateEvent(cannon, message, player, silent))
            return null;

        startCannonCreation(cannon, message, owner, silent);

        Cannon finalCannon = cannon;
        var taskManager = AsyncTaskManager.get();
        taskManager.fireSyncRunnable(() -> {
            CannonAfterCreateEvent caceEvent = new CannonAfterCreateEvent(finalCannon, player.getUniqueId());
            Bukkit.getServer().getPluginManager().callEvent(caceEvent);
        });

        plugin.logDebug("Time to find cannon: " + new DecimalFormat("0.00").format((System.nanoTime() - startTime) / 1000000.0) + "ms");

        return finalCannon;
    }

    private boolean fireCannonBeforeCreateEvent(Cannon cannon, MessageEnum message, Player player, boolean silent) {
        plugin.logDebug("CannonBeforeCreateEvent Cannon: " + cannon + "message: " + message + " player: " + player);
        plugin.logDebug("player.getUniqueId(): " + player.getUniqueId());

        var taskManager = AsyncTaskManager.get();
        CannonBeforeCreateEvent cbceEvent = taskManager.fireSyncSupplier(() -> {
            CannonBeforeCreateEvent event = new CannonBeforeCreateEvent(cannon, message, player.getUniqueId());
            Bukkit.getServer().getPluginManager().callEvent(event);
            return event;
        });

        //add cannon to the list if everything was fine and return the cannon
        if (!cbceEvent.isCancelled() && cbceEvent.getMessage() != null && cbceEvent.getMessage() == MessageEnum.CannonCreated) {
            return true;
        }

        //send messages
        if (!silent) {
            taskManager.scheduler.runTask(player, () -> {
                userMessages.sendMessage(message, player, cannon);
                SoundUtils.playErrorSound(cannon.getMuzzle());
            });
        }

        plugin.logDebug("Creating a cannon event was canceled: " + message);
        return false;
    }

    private void startCannonCreation(Cannon cannon, MessageEnum message, UUID owner, boolean silent) {
        plugin.logDebug("a new cannon was created by " + cannon.getOwner());
        createCannon(cannon, true);

        //send messages
        var taskManager = AsyncTaskManager.get();
        if (!silent) {
            Player player = Bukkit.getPlayer(owner);
            taskManager.scheduler.runTask(player, () -> {
                userMessages.sendMessage(message, owner, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundCreate());
            });
        }
    }

    /**
     * returns the cannon from the storage
     *
     * @param uid UUID of the cannon
     * @return the cannon from the storage
     */
    public static Cannon getCannon(UUID uid) {
        if (uid == null)
            return null;

        return cannonList.get(uid);
    }

    /**
     * searches if this block is part of a cannon and create a new one
     *
     * @param cannonBlock block of the cannon
     * @param owner       the player who will be the owner of the cannon if it is a new cannon
     * @return cannon if found, else null
     */
    private Cannon checkCannon(Location cannonBlock, UUID owner) {
        // is this block material used for a cannon design
        Block block = cannonBlock.getBlock();
        IBlock blockStuff = SchematicWorldProcessorImpl
            .getProcessor()
            .registry()
            .getBlock(block.getX(), block.getY(), block.getZ(), block.getWorld().getUID());

        if (!isValidCannonBlock(block))
            return null;

        World world = cannonBlock.getWorld();
        var designList = DesignStorage.getInstance().getCannonDesignList();
        designList = designList.stream().filter(it -> it.isAllowedMaterial(block.getType())).toList();

        // check all cannon design if this block is part of the design
        for (CannonDesign cannonDesign : designList) {
            // check of all directions
            for (BlockFace cannonDirection : blockFaces) {
                // for all blocks for the design
                List<SimpleBlock> designBlockList = cannonDesign.getAllCannonBlocks(cannonDirection);
                //check for empty entries
                if (designBlockList.isEmpty()) {
                    plugin.logSevere("There are empty cannon design schematics in your design folder. Please check it.");
                    return null;
                }

                Schematic schem = cannonDesign.getSchematicMap().get(cannonDirection);
                for (IBlock designBlock : schem) {
                    // compare blocks
                    if (!designBlock.key().equals(blockStuff.key())) {
                        continue;
                    }

                    // this block is same as in the design, get the offset
                    Vector offset = new Vector(
                        cannonBlock.getBlockX() - designBlock.x(),
                        cannonBlock.getBlockY() - designBlock.y(),
                        cannonBlock.getBlockZ() - designBlock.z()
                    );

                    OffsetSchematic offsetSchematic = new OffsetSchematicImpl(
                        offset.getBlockX(),
                        offset.getBlockY(),
                        offset.getBlockZ(),
                        schem.positions()
                    );

                    boolean matches = SchematicWorldProcessorImpl.getProcessor().matches(offsetSchematic, world.getUID());
                    if (matches) {
                        return new Cannon(
                            cannonDesign,
                            world.getUID(),
                            offset,
                            cannonDirection,
                            owner
                        );
                    }
                }
            }
        }
        return null;
    }

    public boolean isValidCannonBlock(Block block) {
        return block != null && DesignStorage.getInstance().isCannonBlockMaterial(block.getType());
    }

    /**
     * returns the number of owned cannons of a player
     *
     * @param player the owner of the cannons
     * @return number of cannons
     */
    public int getNumberOfCannons(UUID player) {
        int i = 0;
        for (Cannon cannon : cannonList.values()) {
            if (cannon.getOwner() == null) {
                plugin.logSevere("Cannon has no owner. Contact the plugin developer");
            } else if (cannon.getOwner().equals(player)) {
                i++;
            }
        }
        return i;
    }

    /**
     * @return List of cannons
     */
    public static ConcurrentHashMap<UUID, Cannon> getCannonList() {
        return cannonList;
    }

    /**
     * List of cannons
     */
    public void clearCannonList() {
        cannonList.clear();
    }

    /**
     * returns the number of cannons manged by the plugin
     *
     * @return number of cannons in all world
     */
    public int getCannonListSize() {
        return cannonList.size();
    }

    /**
     * returns the amount of cannons a player can build
     *
     * @param player check the number of cannon this player can build
     * @return the maximum number of cannons
     */
    public int getCannonBuiltLimit(Player player) {
        // the player is not valid - no limit check
        if (player == null) return Integer.MAX_VALUE;

        // both limitA/B and cannons.limit.5 work
        // if all notes are enabled, set limit to a high number. If no permission plugin is loaded, everything is enabled

        int newBuiltLimit = getNewBuildLimit(player);

        // config implementation
        if (config.isBuildLimitEnabled()) {
            plugin.logDebug("BuildLimit: limitA and limitB are enabled");
            int buildLimitB = config.getBuildLimitB();
            int buildLimitA = config.getBuildLimitA();

            if (player.hasPermission("cannons.limit.limitB") && (newBuiltLimit > buildLimitB)) {
                // return the
                plugin.logDebug("build limitB sets the number of cannons to: " + buildLimitB);
                return buildLimitB;
            }

            if (player.hasPermission("cannons.limit.limitA") && (newBuiltLimit > buildLimitA)) {
                // limit B is stronger
                plugin.logDebug("build limitA sets the number of cannons to: " + buildLimitA);
                return buildLimitA;
            }
        }
        // player implementation
        if (newBuiltLimit >= 0) {
            plugin.logDebug("permission build limit sets the maximum number of cannons to: " + newBuiltLimit);
            return newBuiltLimit;
        }
        plugin.logDebug("no build limit found. Setting to max value.");
        return Integer.MAX_VALUE;
    }

    /**
     * returns the build limit for the player given by cannons.limit.5
     *
     * @param player the build limit for this player
     * @return how many cannosn this player can build
     */
    public int getNewBuildLimit(Player player) {
        if (player == null) return -1;

        if (player.hasPermission("cannons.limit." + Integer.MAX_VALUE)) {
            //all nodes are enabled
            plugin.logDebug("BuildLimit: all entries for cannons.limit.x are TRUE. Returning max build limit.");
            return Integer.MAX_VALUE;
        }

        // else check all nodes for the player
        for (int i = 100; i >= 0; i--) {
            if (player.hasPermission("cannons.limit." + i)) {
                plugin.logDebug("BuildLimit: entry for cannons.limit." + i + " found");
                return i;
            }
        }
        //no build limit found
        plugin.logDebug("BuildLimit: no entry for cannons.limit.x found");
        return Integer.MAX_VALUE;
    }

    /**
     * checks if the player can build a cannon, checks permission and builtLimit
     */
    public MessageEnum canBuildCannon(Cannon cannon, UUID owner) {
        CannonDesign design = cannon.getCannonDesign();

        //get the player from the server
        if (owner == null) return null;
        Player player = Bukkit.getPlayer(owner);
        if (player == null) return null;

        // check if player has permission to build
        if (!player.hasPermission(design.getPermissionBuild())) {
            return MessageEnum.PermissionErrorBuild;
        }
        // player does not have too many guns
        if (getNumberOfCannons(owner) >= getCannonBuiltLimit(player)) {
            return MessageEnum.ErrorCannonBuiltLimit;
        }
        // player has sufficient permission to build a cannon
        return MessageEnum.CannonCreated;
    }

    /**
     * removes all cannon
     */
    public void deleteAllCannons() {
        Iterator<Cannon> iter = cannonList.values().iterator();

        while (iter.hasNext()) {
            Cannon cannon = iter.next();
            OfflinePlayer offplayer = Bukkit.getOfflinePlayer(cannon.getOwner());
            // return money to the player if the cannon was paid
            var buildingCost = cannon.getCannonDesign().getEconomyBuildingCost();

            if (offplayer != null && offplayer.hasPlayedBefore() && cannon.isPaid()) {
                buildingCost.execute(offplayer, cannon);
            }
            cannon.destroyCannon(false, false, BreakCause.Other);
            iter.remove();
        }
    }


    /**
     * Deletes all cannons of this player in the database to reset the cannon limit
     *
     * @param owner the owner of the cannon
     * @return returns true if there was an entry of this player in the list
     */
    public boolean deleteCannons(UUID owner) {
        Iterator<Cannon> iter = cannonList.values().iterator();
        boolean inList = false;

        while (iter.hasNext()) {
            Cannon next = iter.next();
            if (next.getOwner() != null && next.getOwner().equals(owner)) {
                inList = true;
                next.destroyCannon(false, false, BreakCause.Other);
                iter.remove();
            }
        }
        return inList;
    }

    /**
     * reloads designs from the design list and updates all entries in the cannon
     */
    public void updateCannons() {
        for (Cannon cannon : cannonList.values()) {
            cannon.setCannonDesign(DesignStorage.getInstance().getDesign(cannon));
            if (cannon.getLoadedProjectile() != null) {
                ItemHolder item = cannon.getLoadedProjectile().getLoadingItem();
                cannon.setLoadedProjectile(plugin.getProjectile(cannon, item));
            }
        }
    }
}