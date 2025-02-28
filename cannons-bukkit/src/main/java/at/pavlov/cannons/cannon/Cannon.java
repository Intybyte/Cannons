package at.pavlov.cannons.cannon;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.Enum.CannonRotation;
import at.pavlov.cannons.Enum.InteractAction;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.cannon.data.AimingData;
import at.pavlov.cannons.cannon.data.AmmoLoadingData;
import at.pavlov.cannons.cannon.data.AngleData;
import at.pavlov.cannons.cannon.data.CannonMainData;
import at.pavlov.cannons.cannon.data.CannonPosition;
import at.pavlov.cannons.cannon.data.LinkingData;
import at.pavlov.cannons.cannon.data.SentryData;
import at.pavlov.cannons.cannon.data.WhitelistData;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SimpleBlock;
import at.pavlov.cannons.cannon.data.FiringData;
import at.pavlov.cannons.event.CannonDestroyedEvent;
import at.pavlov.cannons.event.CannonGunpowderLoadEvent;
import at.pavlov.cannons.event.CannonPreLoadEvent;
import at.pavlov.cannons.event.CannonUseEvent;
import at.pavlov.cannons.interfaces.ICannon;
import at.pavlov.cannons.interfaces.functionalities.Rotational;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileStorage;
import at.pavlov.cannons.utils.CannonsUtil;
import at.pavlov.cannons.utils.InventoryManagement;
import at.pavlov.cannons.utils.SoundUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Attachable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Cannon implements ICannon, Rotational {

    private CannonMainData mainData;
    private CannonPosition cannonPosition;

    private AmmoLoadingData ammoLoadingData = new AmmoLoadingData();
    private FiringData firingData = new FiringData();
    private AimingData aimingData = new AimingData();
    private AngleData angleData = new AngleData();

    private SentryData sentryData = new SentryData();

    private WhitelistData whitelistData = new WhitelistData();
    private LinkingData linkingData = new LinkingData();
    //observer will see the impact of the target predictor
    //<Player name, remove after showing impact>
    private final HashMap<UUID, Boolean> observerMap = new HashMap<>();
    // spread multiplier from the last operator of the cannon
    private double lastPlayerSpreadMultiplier;

    // has the cannon entry changed since it was last saved in the database
    @Setter
    @Getter
    private boolean updated;

    private CannonDesign design;
    private final Random random = new Random();
    //TODO make a vector util class and add this there
    private final static Vector noVelocity = new Vector(0,0,0);


    public Cannon(CannonDesign design, UUID world, Vector cannonOffset, BlockFace cannonDirection, UUID owner) {

        this.design = design;
        this.cannonPosition = new CannonPosition(cannonDirection, cannonOffset, world, false, noVelocity.clone());
        boolean feePresent = design.getEconomyBuildingCost() <= 0;
        this.mainData = new CannonMainData( UUID.randomUUID(),null, feePresent, owner, true);

        // set owner in the whitelist
        if (feePresent)
            whitelistData.add(owner);

        this.sentryData.setSentryEntity(null);
        this.sentryData.setSentryEntityHistory(new ArrayList<>());

        this.angleData.setHorizontalAngle(getHomeHorizontalAngle());
        this.angleData.setVerticalAngle(getHomeVerticalAngle());

        this.lastPlayerSpreadMultiplier = 1.0;

        // reset
        int defaultLoadedGunpowder = !design.isGunpowderNeeded() || design.isPreloaded() ? design.getMaxLoadableGunpowderNormal() : 0;
        this.ammoLoadingData.setLoadedGunpowder(defaultLoadedGunpowder);

        Projectile defaultLoadedProjectile = design.isPreloaded() ? this.getDefaultProjectile(this) : null;
        this.ammoLoadingData.setLoadedProjectile(defaultLoadedProjectile);
        firingData.setLastFiredProjectile(null);
        firingData.setLastFiredGunpowder(0);
        this.setSoot(design.getStartingSoot());
        this.setProjectilePushed(design.getProjectilePushing());

        this.sentryData.setTargetMob(true);
        this.sentryData.setTargetPlayer(false);
        this.sentryData.setTargetCannon(false);

        this.updated = true;
    }


    /**
     * returns the location of the location of the cannon
     *
     * @return location of the cannon
     */
    public Location getLocation() {
        return design.getAllCannonBlocks(this).get(0);
    }

    /**
     * returns the location of the muzzle
     *
     * @return location of the muzzle
     */
    public Location getMuzzle() {
        return design.getMuzzle(this);
    }

    /**
     * returns a random block of the barrel or the cannon if there is no barrel
     *
     * @return location of the barrel block
     */
    public Location getRandomBarrelBlock() {
        List<Location> barrel = design.getBarrelBlocks(this);
        if (!barrel.isEmpty())
            return barrel.get(random.nextInt(barrel.size()));
        List<Location> all = design.getAllCannonBlocks(this);
        return all.get(random.nextInt(all.size()));
    }


    /**
     * removes the loaded charge form the chest attached to the cannon, returns true if the ammo was found in the chest
     *
     * @param player       - player operating the cannon
     * @param consumesAmmo - if true ammo will be removed from chest inventories
     * @return - true if the cannon has been reloaded. False if there is not enough ammunition
     */
    public MessageEnum reloadFromChests(UUID player, boolean consumesAmmo) {
        List<Inventory> invlist = getInventoryList();

        if (!isClean())
            return MessageEnum.ErrorNotCleaned;

        if (isLoading())
            return MessageEnum.ErrorLoadingInProgress;

        if (isFiring())
            return MessageEnum.ErrorFiringInProgress;

        if (!isProjectilePushed() && isLoaded()) {
            this.setProjectilePushed(0);
            return MessageEnum.RamrodPushingProjectileDone;
        }

        if (isProjectilePushed() && isLoaded())
            return MessageEnum.ErrorProjectileAlreadyLoaded;

        //load gunpowder if there is nothing in the barrel
        MessageEnum gunpowder = loadGunpowderBarrel(consumesAmmo, invlist);
        if (gunpowder!=null)
            return gunpowder;

        // find a loadable projectile in the chests
        for (Inventory inv : invlist) {
            for (ItemStack item : inv.getContents()) {
                Projectile projectile = ProjectileStorage.getProjectile(this, item);
                if (projectile == null)
                    continue;

                MessageEnum message = checkPermProjectile(projectile, player);
                if (message != MessageEnum.loadProjectile) {
                    continue;
                }
                // everything went fine, so remove it from the chest remove projectile
                setLoadedProjectile(projectile);
                if (design.isProjectileConsumption() && consumesAmmo) {
                    if (item.getAmount() == 1) {
                        //last item removed
                        inv.removeItem(item);
                    } else {
                        //remove one item
                        item.setAmount(item.getAmount() - 1);
                    }
                }
                //push projectile and done
                setProjectilePushed(0);
                SoundUtils.playSound(getMuzzle(), getLoadedProjectile().getSoundLoading());
                firingData.setLastLoaded(System.currentTimeMillis());
                return MessageEnum.loadProjectile;
            }
        }
        return MessageEnum.ErrorNoProjectileInChest;
    }

    private MessageEnum loadGunpowderBarrel(boolean consumesAmmo, List<Inventory> invlist) {
        //load gunpowder if there is nothing in the barrel
        if (!design.isGunpowderConsumption() || !design.isGunpowderNeeded() || !consumesAmmo) {
            //no ammo consumption - only load if there is less gunpowder then normal in the barrel
            if (getLoadedGunpowder() <= design.getMaxLoadableGunpowderNormal())
                ammoLoadingData.setLoadedGunpowder(design.getMaxLoadableGunpowderNormal());
            return null;
        }

        //gunpowder will be consumed from the inventory
        //load the maximum gunpowder possible (maximum amount that fits in the cannon or is in the chest)
        int toLoad = design.getMaxLoadableGunpowderNormal() - getLoadedGunpowder();

        if (toLoad <= 0) {
            return null;
        }

        ItemStack gunpowder = design.getGunpowderType().toItemStack(toLoad);
        Cannons.getPlugin().logDebug("Amount of chests next to cannon: " + invlist.size());
        gunpowder = InventoryManagement.removeItem(invlist, gunpowder);

        if (gunpowder.getAmount() == 0) {
            //there was enough gunpowder in the chest
            ammoLoadingData.setLoadedGunpowder(design.getMaxLoadableGunpowderNormal());
            return null;
        }

        //not enough gunpowder, put it back
        gunpowder.setAmount(toLoad - gunpowder.getAmount());
        InventoryManagement.addItemInChests(invlist, gunpowder);
        return MessageEnum.ErrorNoGunpowderInChest;
    }

    @Override
    public boolean automaticCooling() {
        return automaticCoolingFromChest();
    }

    /**
     * removes cooling item form the chest attached to the cannon, returns true if it was enough to cool down the cannon
     *
     * @return - true if the cannon has been cooled down
     */
    @Deprecated(forRemoval = true)
    public boolean automaticCoolingFromChest() {

        List<Inventory> invlist = getInventoryList();

        //cooling items will be consumed from the inventory
        int toCool = (int) Math.ceil((this.getTemperature() - design.getWarningTemperature()) / design.getCoolingAmount());
        ItemStack item = new ItemStack(Material.AIR, toCool);

        if (toCool <= 0)
            return true;

        //do this for every cooling item
        for (ItemHolder mat : design.getItemCooling()) {
            if (mat == null)
                continue;

            int itemAmount = item.getAmount();
            if (itemAmount <= 0)
                continue;

            item = mat.toItemStack(itemAmount);
            item = InventoryManagement.removeItem(invlist, item);

            int usedItems = toCool - item.getAmount();
            this.setTemperature(this.getTemperature() - usedItems * design.getCoolingAmount());

            //put used items back to the chest (not if the item is AIR)
            ItemStack itemUsed = design.getCoolingToolUsed(item);
            itemUsed.setAmount(usedItems);
            if (!itemUsed.getType().equals(Material.AIR))
                InventoryManagement.addItemInChests(invlist, itemUsed);

            //if all items have been removed we are done
            if (item.getAmount() == 0)
                return true;
        }
        return false;
    }


    /**
     * returns the inventories of all attached chests
     *
     * @return - list of inventory
     */
    public List<Inventory> getInventoryList() {
        //get the inventories of all attached chests
        List<Inventory> invlist = new ArrayList<>();
        for (Location loc : getCannonDesign().getChestsAndSigns(this)) {
            // check if block is a chest
            invlist = InventoryManagement.getInventories(loc.getBlock(), invlist);
        }
        return invlist;
    }


    /**
     * loads Gunpowder in a cannon
     *
     * @param amountToLoad - number of items which are loaded into the cannon
     * @param player - Player loading the cannon
     */
    private MessageEnum getGunpowderMessage(int amountToLoad, Player player) {
        //this cannon does not need gunpowder
        if (!design.isGunpowderNeeded())
            return MessageEnum.ErrorNoGunpowderNeeded;
        // this cannon needs to be cleaned first
        if (!isClean())
            return MessageEnum.ErrorNotCleaned;
        if (isLoading())
            return MessageEnum.ErrorLoadingInProgress;
        if (isFiring())
            return MessageEnum.ErrorFiringInProgress;
        //projectile pushing necessary
        if (isLoaded() && !isProjectilePushed())
            return MessageEnum.ErrorNotPushed;
        // projectile already loaded
        if (isLoaded())
            return MessageEnum.ErrorProjectileAlreadyLoaded;
        // maximum gunpowder already loaded
        if (getLoadedGunpowder() >= design.getMaxLoadableGunpowderOverloaded())
            return MessageEnum.ErrorMaximumGunpowderLoaded;

        //load the maximum gunpowder


        int gunpowder = Math.min(getLoadedGunpowder() + amountToLoad, design.getMaxLoadableGunpowderOverloaded());
        CannonGunpowderLoadEvent event = new CannonGunpowderLoadEvent(this, getLoadedGunpowder(), amountToLoad, gunpowder, player);
        Bukkit.getServer().getPluginManager().callEvent(event);

        setLoadedGunpowder(gunpowder);

        //Overloading is enabled
        if (!design.isOverloadingEnabled()) {
            return MessageEnum.loadGunpowder;
        }

        if (design.getMaxLoadableGunpowderNormal() < getLoadedGunpowder()) {
            return MessageEnum.loadOverloadedGunpowder;
        }

        if (!design.isOverloadingRealMode() && design.getMaxLoadableGunpowderNormal() == getLoadedGunpowder()) {
            return MessageEnum.loadGunpowderNormalLimit;
        }

        return MessageEnum.loadGunpowder;
    }


    /**
     * checks the permission of a player before loading gunpowder in the cannon. Designed for player operation
     *
     * @param player - the player which is loading the cannon
     */
    public MessageEnum loadGunpowder(Player player) {

        //fire event
        CannonUseEvent useEvent = new CannonUseEvent(this, player.getUniqueId(), InteractAction.loadGunpowder);
        Bukkit.getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled())
            return null;

        //save the amount of gunpowder we loaded in the cannon
        int gunpowder = 0;
        int maximumLoadableNormal = design.getMaxLoadableGunpowderNormal() - getLoadedGunpowder();
        int maximumLoadableAbsolute = design.getMaxLoadableGunpowderOverloaded() - getLoadedGunpowder();

        //check if the player has permissions for this cannon
        MessageEnum returnVal = checkPermGunpowder(player);

        //the player seems to have all rights to load the cannon.
        if (returnVal.equals(MessageEnum.loadGunpowder)) {
            //if the player is sneaking the maximum gunpowder is loaded, but at least 1
            if (player.isSneaking()) {
                gunpowder = player.getInventory().getItemInMainHand().getAmount();
                if (maximumLoadableNormal < gunpowder) gunpowder = maximumLoadableNormal;
                else if (maximumLoadableAbsolute < gunpowder) gunpowder = 1;
            }
            if (gunpowder <= 0)
                gunpowder = 1;
            if (design.isAutoloadChargeWhenLoadingProjectile()) {
                //get the amount of gunpowder that can be maximal loaded
                if (player.getInventory().contains(design.getGunpowderType().getType(), maximumLoadableNormal))
                    gunpowder = maximumLoadableNormal;
                else
                    gunpowder = 0;
                Cannons.logger().info("gunpowder: " + gunpowder);
            }

            //load the gunpowder
            returnVal = getGunpowderMessage(gunpowder, player);
        }

        // the cannon was loaded with gunpowder - lets get it form the player
        final boolean checkGunpowder = design.isGunpowderConsumption() && !design.isAmmoInfiniteForPlayer();
        switch (returnVal) {
            case loadGunpowder, loadGunpowderNormalLimit -> {
                SoundUtils.playSound(getMuzzle(), design.getSoundGunpowderLoading());
                if (checkGunpowder) {
                    InventoryManagement.removeItem(player.getInventory(), design.getGunpowderType().toItemStack(gunpowder));
                }
            }
            case loadOverloadedGunpowder -> {
                SoundUtils.playSound(getMuzzle(), design.getSoundGunpowderOverloading());
                if (checkGunpowder)
                    InventoryManagement.takeFromPlayerHand(player, gunpowder);
            }
            default -> SoundUtils.playErrorSound(getMuzzle());
        }
        return returnVal;
    }

    /**
     * load the projectile in the cannon and checks permissions. Designed for player operation
     *
     * @param player - who is loading the cannon
     * @return - a message which can be displayed
     */
    public MessageEnum loadProjectile(Projectile projectile, Player player) {
        //fire event
        CannonUseEvent useEvent = new CannonUseEvent(this, player.getUniqueId(), InteractAction.loadProjectile);
        Bukkit.getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled())
            return null;

        if (projectile == null) return null;

        MessageEnum returnVal;

        // autoload gunpowder
        if (design.isAutoloadChargeWhenLoadingProjectile()) {
            returnVal = loadGunpowder(player);
            //return error if loading of gunpowder was not successful
            if (!(returnVal.equals(MessageEnum.loadGunpowder) || returnVal.equals(MessageEnum.loadGunpowderNormalLimit) || returnVal.equals(MessageEnum.loadOverloadedGunpowder)))
                return returnVal;
        }

        CannonPreLoadEvent event = new CannonPreLoadEvent(this, projectile, player);
        Bukkit.getServer().getPluginManager().callEvent(event);
        returnVal = checkPermProjectile(projectile, player);

        // check if loading of projectile was successful
        if (!returnVal.equals(MessageEnum.loadProjectile) && !returnVal.equals(MessageEnum.loadGunpowderAndProjectile)) {
            //projectile not loaded
            SoundUtils.playErrorSound(getMuzzle());
            return returnVal;
        }
        // load projectile
        setLoadedProjectile(projectile);
        SoundUtils.playSound(getMuzzle(), projectile.getSoundLoading());

        // remove from player
        if (design.isProjectileConsumption() && !design.isAmmoInfiniteForPlayer())
            InventoryManagement.takeFromPlayerHand(player, 1);

        return returnVal;
    }

    /**
     * Returns the default projectile for this cannon (first entry).
     *
     * @return default Projectile
     */
    public Projectile getDefaultProjectile(Cannon cannon) {
        if (!this.getCannonDesign().getAllowedProjectiles().isEmpty())
            return ProjectileStorage.getProjectile(cannon, this.getCannonDesign().getAllowedProjectiles().get(0));
        return null;
    }

    /**
     * Check if cannons can be loaded with gunpowder by the player
     *
     * @param player - check permissions of this player
     * @return - true if the cannon can be loaded
     */
    private MessageEnum checkPermGunpowder(Player player) {

        if (player != null) {
            //if the player is not the owner of this gun
            if (design.isAccessForOwnerOnly() && this.getOwner() != null && !this.getOwner().equals(player.getUniqueId()))
                return MessageEnum.ErrorNotTheOwner;
            // player can't load cannon
            if (!player.hasPermission(design.getPermissionLoad()))
                return MessageEnum.PermissionErrorLoad;
        }
        // loading successful
        return MessageEnum.loadGunpowder;
    }

    /**
     * Check if the cannons can be loaded
     *
     * @param playerUid - whose permissions are checked
     * @return true if the player and cannons can load the projectile
     */
    private MessageEnum checkPermProjectile(Projectile projectile, UUID playerUid) {
        if (playerUid == null) {
            return checkPermProjectile(projectile, (Player) null);
        }

        return checkPermProjectile(projectile, Bukkit.getPlayer(playerUid));
    }

    /**
     * Check if the cannons can be loaded
     *
     * @param player - whose permissions are checked
     * @return true if the player and cannons can load the projectile
     */
    private MessageEnum checkPermProjectile(Projectile projectile, Player player) {
        if (player != null) {
            //if the player is not the owner of this gun
            if (this.getOwner() != null && !this.getOwner().equals(player.getUniqueId()) && design.isAccessForOwnerOnly())
                return MessageEnum.ErrorNotTheOwner;
            // no permission for this projectile
            if (!projectile.hasPermission(player))
                return MessageEnum.PermissionErrorProjectile;
        }
        // no gunpowder loaded
        if (!isGunpowderLoaded())
            return MessageEnum.ErrorNoGunpowder;
        if (isLoading())
            return MessageEnum.ErrorLoadingInProgress;
        if (isFiring())
            return MessageEnum.ErrorFiringInProgress;
        // already loaded with a projectile
        if (isLoaded())
            return MessageEnum.ErrorProjectileAlreadyLoaded;
        // is cannon cleaned with ramrod?
        if (!isClean())
            return MessageEnum.ErrorNotCleaned;

        // loading successful
        if (design.isAutoloadChargeWhenLoadingProjectile())
            return MessageEnum.loadGunpowderAndProjectile;
        return MessageEnum.loadProjectile;
    }

    /**
     * Permission check and usage for ram rod
     *
     * @param player player using the ramrod tool (null will bypass permission check)
     * @return message for the player
     */
    private MessageEnum useRamRodInternal(Player player) {
        //no permission to use this tool
        if (player != null && !player.hasPermission(design.getPermissionRamrod()))
            return MessageEnum.PermissionErrorRamrod;
        //if the player is not the owner of this gun
        if (player != null && this.getOwner() != null && !this.getOwner().equals(player.getUniqueId()) && design.isAccessForOwnerOnly())
            return MessageEnum.ErrorNotTheOwner;
        if (isLoading())
            return MessageEnum.ErrorLoadingInProgress;
        if (isFiring())
            return MessageEnum.ErrorFiringInProgress;
        //if the barrel is dirty clean it
        if (!isClean()) {
            cleanCannon(1);
            if (isClean())
                return MessageEnum.RamrodCleaningDone;
            else {
                CannonUseEvent cleaning = new CannonUseEvent(this, player.getUniqueId(), InteractAction.cleaningCannon);
                Bukkit.getServer().getPluginManager().callEvent(cleaning);
                return MessageEnum.RamrodCleaning;
            }
        }
        //if clean show message that cleaning is done
        if (isClean() && !isGunpowderLoaded()) {
            cleanCannon(1);
            return MessageEnum.RamrodCleaningDone;
        }
        //if no projectile
        if (!isLoaded())
            return MessageEnum.ErrorNoProjectile;
        //if the projectile is loaded
        if (!isProjectilePushed()) {
            pushProjectile(1);
            if (isProjectilePushed()) {
                return MessageEnum.RamrodPushingProjectileDone;
            } else {
                CannonUseEvent cleaning = new CannonUseEvent(this, player.getUniqueId(), InteractAction.pushingProjectile);
                Bukkit.getServer().getPluginManager().callEvent(cleaning);
                return MessageEnum.RamrodPushingProjectile;
            }
        }
        //if projectile is in place
        if (isLoaded() && isProjectilePushed())
            return MessageEnum.ErrorProjectileAlreadyLoaded;

        //no matching case found
        return null;

    }

    /**
     * a ramrod is used to clean the barrel before loading gunpowder and to push the projectile into the barrel
     *
     * @param player player using the ramrod tool (null will bypass permission check)
     * @return message for the player
     */
    public MessageEnum useRamRod(Player player) {
        MessageEnum message = useRamRodInternal(player);

        if (message == null) {
            return null;
        }

        if (message.isError()) {
            SoundUtils.playErrorSound(getMuzzle());
            return message;
        }

        switch (message) {
            case RamrodCleaning -> SoundUtils.playSound(getMuzzle(), design.getSoundRamrodCleaning());
            case RamrodCleaningDone -> SoundUtils.playSound(getMuzzle(), design.getSoundRamrodCleaningDone());
            case RamrodPushingProjectile -> SoundUtils.playSound(getMuzzle(), design.getSoundRamrodPushing());
            case RamrodPushingProjectileDone -> SoundUtils.playSound(getMuzzle(), design.getSoundRamrodPushingDone());
            default -> SoundUtils.playErrorSound(getMuzzle());
        }
        return message;
    }

    /**
     * returns true if the cannon has at least 1 gunpowder loaded
     *
     * @return true if loaded with gunpowder
     */
    public boolean isGunpowderLoaded() {
        return getLoadedGunpowder() > 0 || !design.isGunpowderNeeded();
    }

    /**
     * removes gunpowder and the projectile. Items are drop at the cannonball firing point
     */
    private void dropCharge() {
        int loadedGunpowder = ammoLoadingData.getLoadedGunpowder();
        //drop gunpowder
        if (loadedGunpowder > 0 && design.isGunpowderNeeded()) {
            ItemStack powder = design.getGunpowderType().toItemStack(loadedGunpowder);
            getWorldBukkit().dropItemNaturally(design.getMuzzle(this), powder);
        }

        // drop projectile
        if (isLoaded()) {
            getWorldBukkit().dropItemNaturally(design.getMuzzle(this), ammoLoadingData.getLoadedProjectile().getLoadingItem().toItemStack(1));
        }
        removeCharge();

    }

    /**
     * removes the gunpowder and projectile loaded in the cannon
     */
    public void removeCharge() {
        firingData.setLastFiredProjectile(getLoadedProjectile());
        firingData.setLastFiredGunpowder(this.getLoadedGunpowder());
        //delete charge for human gunner
        if (design.isGunpowderNeeded())
            this.setLoadedGunpowder(0);
        this.setLoadedProjectile(null);
    }

    /**
     * removes the sign text and charge of the cannon after destruction
     *
     * @param breakBlocks break all cannon block naturally
     * @param canExplode  if the cannon can explode when loaded with gunpoweder
     * @param cause       cause of the cannon destruction
     */
    public MessageEnum destroyCannon(boolean breakBlocks, boolean canExplode, BreakCause cause) {
        // update cannon signs the last time
        setValid(false);

        //fire and an event that this cannon is destroyed
        CannonDestroyedEvent destroyedEvent = new CannonDestroyedEvent(this, cause, breakBlocks, canExplode);
        Bukkit.getServer().getPluginManager().callEvent(destroyedEvent);

        if (breakBlocks)
            breakAllCannonBlocks();

        //loaded cannon can exploded (80% chance)
        if (canExplode && design.getExplodingLoadedCannons() > 0 && getLoadedGunpowder() > 0 && Math.random() > 0.2) {
            double power = 1.0 * getLoadedGunpowder() / design.getMaxLoadableGunpowderNormal() * design.getExplodingLoadedCannons();
            World world = getWorldBukkit();
            if (world != null)
                //todo fix overheating
                world.createExplosion(getRandomBarrelBlock(), (float) power);
        } else {
            // drop charge
            dropCharge();
        }

        // return message
        return switch (cause) {
            case Overheating -> MessageEnum.HeatManagementOverheated;
            case Other -> null;
            case Dismantling -> MessageEnum.CannonDismantled;
            default -> MessageEnum.CannonDestroyed;
        };
    }

    /**
     * this will force the cannon to show up at this location - all blocks will be overwritten
     */
    public void show() {
        for (SimpleBlock cBlock : design.getAllCannonBlocks(this.getCannonDirection())) {
            Block wBlock = cBlock.toLocation(getWorldBukkit(), getOffset()).getBlock();
            //todo check show
            wBlock.setBlockData(cBlock.getBlockData());
            //wBlock.setBlockData(cBlock);
        }
    }

    /**
     * this will force the cannon blocks to become AIR
     */
    public void hide() {
        //remove only attachable block
        for (SimpleBlock cBlock : design.getAllCannonBlocks(this.getCannonDirection())) {
            Block wBlock = cBlock.toLocation(getWorldBukkit(), getOffset()).getBlock();
            //if that block is not loaded

            if (wBlock.getState() instanceof Attachable) {
                //Cannons.logger().info("hide " + wBlock.getType());
                wBlock.setType(Material.AIR);
                //wBlock.setData((byte) 0, false);
            }
        }

        //remove all
        for (SimpleBlock cBlock : design.getAllCannonBlocks(this.getCannonDirection())) {
            Block wBlock = cBlock.toLocation(getWorldBukkit(), getOffset()).getBlock();

            if (wBlock.getType() != Material.AIR) {
                wBlock.setType(Material.AIR);
                // wBlock.setData((byte) 0, false);
            }
        }
    }


    /**
     * breaks all cannon blocks of the cannon
     */
    private void breakAllCannonBlocks() {
        List<Location> locList = design.getAllCannonBlocks(this);
        for (Location loc : locList) {
            loc.getBlock().breakNaturally();
        }
    }


    /**
     * returns true if this block is a block of the cannon
     *
     * @param block - block to check
     * @return - true if it is part of this cannon
     */
    public boolean isCannonBlock(Block block) {
        if (!getWorld().equals(block.getWorld().getUID())) {
            return false;
        }

        for (SimpleBlock designBlock : design.getAllCannonBlocks(getCannonDirection())) {
            if (designBlock.compareMaterialAndLoc(block, getOffset())) {
                return true;
            }
        }
        return false;
    }

    /**
     * return true if this block can be destroyed, false if it is protected
     *
     * @param block - location of the block
     * @return - true if the block can be destroyed
     */
    public boolean isDestructibleBlock(Location block) {
        for (Location loc : design.getDestructibleBlocks(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * return true if this block is a part of the loading interface - default is
     * the barrel the barrel
     *
     * @param block
     * @return true if this block is a part of the loading interface
     */
    public boolean isLoadingBlock(Location block) {
        for (Location loc : design.getLoadingInterface(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }


    /**
     * return true if this location where the torch interacts with the cannon
     *
     * @param block
     * @return true if this location where the torch interacts with the cannon
     */
    public boolean isChestInterface(Location block) {
        for (Location loc : design.getChestsAndSigns(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * true if this location where there is a cannon status sign
     * does not check the ID
     *
     * @param loc
     * @return true if this location where there is a cannon status sign
     */
    public boolean isCannonSign(Location loc) {
        if (!(loc.getBlock().getBlockData() instanceof WallSign)) {
            return false;
        }

        CannonBlocks cannonBlocks = this.getCannonDesign().getCannonBlockMap().get(this.getCannonDirection());

        if (cannonBlocks == null) {
            return false;
        }

        for (SimpleBlock cannonblock : cannonBlocks.getChestsAndSigns()) {
            // compare location
            if (cannonblock.toLocation(this.getWorldBukkit(), this.getOffset()).equals(loc)) {
                //Block block = loc.getBlock();
                //compare and data
                //only the two lower bits of the bytes are important for the direction (delays are not interessting here)
                //if (cannonblock.getData() == block.getData() || block.getData() == -1 || cannonblock.getData() == -1 )
                return true;
            }
        }
        return false;
    }

    /**
     * @param block
     * @return true if this is a right click trigger block
     */
    public boolean isRightClickTrigger(Location block) {
        for (Location loc : design.getRightClickTrigger(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param block
     * @return true if this is a redstone trigger block
     */
    public boolean isRestoneTrigger(Location block) {
        for (Location loc : design.getRedstoneTrigger(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param block
     * @return true if this location where the torch interacts with the cannon
     */
    public boolean isRedstoneTorchInterface(Location block) {
        for (Location loc : design.getRedstoneTorches(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param block
     * @return true if this location where a redstone torch interacts with the cannon
     */
    public boolean isRedstoneWireInterface(Location block) {
        for (Location loc : design.getRedstoneWireAndRepeater(this)) {
            if (loc.equals(block)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param loc
     * @return true if this location where the repeater interacts with the cannon
     */
    public boolean isRedstoneRepeaterInterface(Location loc) {
        CannonBlocks cannonBlocks = this.getCannonDesign().getCannonBlockMap().get(this.getCannonDirection());

        if (cannonBlocks == null) {
            return false;
        }

        for (SimpleBlock cannonblock : cannonBlocks.getRedstoneWiresAndRepeater()) {
            // compare location
            if (cannonblock.toLocation(this.getWorldBukkit(), this.getOffset()).equals(loc)) {
                //Block block = loc.getBlock();
                //compare and data
                //only the two lower bits of the bytes are important for the direction (delays are not interessting here)
                //if (cannonblock.getData() == block.getData() %4 || block.getData() == -1 || cannonblock.getData() == -1 )
                return true;
            }
        }
        return false;
    }

    /**
     * returns true if the sentry is in automatic mode, false if in manual mode
     *
     * @return true if sentry is in automatic mode
     */
    public boolean isSentryAutomatic() {
        for (Location loc : design.getBarrelBlocks(this)) {
            if (loc.getBlock().isBlockIndirectlyPowered())
                return false;
        }
        return true;
    }

    /**
     * returns the first block of the cannon
     *
     * @return - first block of the cannon
     */
    public Location getFirstCannonBlock() {
        return design.getAllCannonBlocks(getCannonDirection()).get(0).toLocation(getWorldBukkit(), getOffset());

    }

    /**
     * returns true if the player has the permission to place redstone near a cannon.
     * player = null will also return true
     *
     * @param player player operating the cannon
     * @return
     */
    public MessageEnum checkRedstonePermission(UUID player) {
        Player playerBukkit = null;
        if (player != null) playerBukkit = Bukkit.getPlayer(player);
        return checkRedstonePermission(playerBukkit);
    }

    /**
     * checks if the player has permission to use the cannon with redstone
     *
     * @return message for the player
     */
    MessageEnum checkRedstonePermission(Player player) {
        // the player is null means he is offline -> automatic handling like
        // database check
        if (player == null) return MessageEnum.CannonCreated;
        // if the player has the permission to use redstone return
        if (player.hasPermission(design.getPermissionRedstone())) return MessageEnum.CannonCreated;

        // torch
        for (Location loc : design.getRedstoneTorches(this)) {
            Material b = loc.getBlock().getType();
            if (b == Material.REDSTONE_TORCH) {
                removeRedstone();
                return MessageEnum.PermissionErrorRedstone;
            }
        }

        // wire
        for (Location loc : design.getRedstoneWireAndRepeater(this)) {
            Material b = loc.getBlock().getType();
            if (b == Material.REDSTONE_WIRE || b == Material.REPEATER || b == Material.COMPARATOR) {
                removeRedstone();
                return MessageEnum.PermissionErrorRedstone;
            }
        }

        // no redstone wiring found
        return MessageEnum.CannonCreated;
    }

    /**
     * break all redstone connections to this cannon
     */
    private void removeRedstone() {
        // torches
        for (Location loc : design.getRedstoneTorches(this)) {
            Block block = loc.getBlock();
            if (block.getType() == Material.REDSTONE_TORCH) {
                block.breakNaturally();
            }
        }

        // wires and repeater
        for (Location loc : design.getRedstoneWireAndRepeater(this)) {
            Block block = loc.getBlock();
            if (block.getType() == Material.REDSTONE_WIRE || block.getType() == Material.REPEATER || block.getType() == Material.COMPARATOR) {
                block.breakNaturally();
            }
        }
    }

    /**
     * updates the rotation of the cannon
     *
     * @param center - center of the rotation
     * @param rotation  - how far the cannon is rotated in degree (90, 180, 270, -90)
     */
    @Override
    public void rotate(Vector center, CannonRotation rotation) {
        int angle = rotation.getAngle();
        double dAngle = angle * Math.PI / 180;

        center = new Vector(center.getBlockX(), center.getBlockY(), center.getBlockZ());

        Vector diffToCenter = getOffset().clone().subtract(center);

        double newX = diffToCenter.getX() * Math.cos(dAngle) - diffToCenter.getZ() * Math.sin(dAngle);
        double newZ = diffToCenter.getX() * Math.sin(dAngle) + diffToCenter.getZ() * Math.cos(dAngle);

        setOffset(new Vector(Math.round(center.getX() + newX), getOffset().getBlockY(), Math.round(center.getZ() + newZ)));

        //rotate blockface
        if (angle > 0) {
            for (int i = 0; i <= angle % 90; i++)
                setCannonDirection(CannonsUtil.roatateFace(getCannonDirection()));
        } else {
            for (int i = 0; i <= (-angle) % 90; i++)
                setCannonDirection(CannonsUtil.roatateFaceOpposite(getCannonDirection()));
        }
        this.hasUpdated();
    }

    /**
     * get the change for a barrel explosion due to overheating
     *
     * @return chance for a barrel explosion
     */
    public double getOverheatingChance() {
        if (!design.isHeatManagementEnabled())
            return 0.0;

        double tempCannon = this.getTemperature();
        double tempCritical = design.getCriticalTemperature();
        double tempMax = design.getMaximumTemperature();
        double explodingProbability = 0.0;

        if (tempCannon > tempCritical) {
            //no exploding chance for temperature < critical, 100% chance for > maximum
            explodingProbability = Math.pow((tempCannon - tempCritical) / (tempMax - tempCritical), 3);
        }
        return explodingProbability;
    }

    /**
     * Checks the cannon if the actual temperature might destroy the cannon
     *
     * @return true if the cannon will explode
     */
    public boolean checkHeatManagement() {
        double explodingProbability = getOverheatingChance();
        //play some effects for a hot barrel
        if (getTemperature() > design.getCriticalTemperature())
            this.playBarrelSmokeEffect((int) (explodingProbability * 20.0 + 1));
        return Math.random() < explodingProbability;
    }

    /**
     * plays the given effect on random locations of the barrel
     *
     * @param amount - number of effects
     */
    void playBarrelSmokeEffect(int amount) {
        if (amount <= 0)
            return;

        List<Location> barrelList = design.getBarrelBlocks(this);

        //if the barrel list is 0 something is completely odd
        int max = barrelList.size();

        Location effectLoc;
        BlockFace face;

        for (int i = 0; i < amount; i++) {
            //grab a random face and find a block for them the adjacent block is AIR
            face = CannonsUtil.randomBlockFaceNoDown();
            //int j = 0;
            do {
                i++;
                effectLoc = barrelList.get(random.nextInt(max)).getBlock().getRelative(face).getLocation();
            } while (i < 4 && effectLoc.getBlock().getType() != Material.AIR);

            effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, face);
            SoundUtils.playSound(effectLoc, design.getSoundHot());
        }
    }

    /**
     * cools down a cannon by using the item in hand of a player
     *
     * @param player player using the cannon
     */
    public boolean coolCannon(Player player) {
        int toCool = (int) Math.ceil((this.getTemperature() - design.getWarningTemperature()) / design.getCoolingAmount());
        if (toCool <= 0)
            return false;

        //if the player is sneaking the maximum gunpowder is loaded, but at least 1
        int amount = 1;
        if (player.isSneaking()) {
            //get the amount of gunpowder that can be maximal loaded
            amount = player.getInventory().getItemInMainHand().getAmount();
            if (amount > toCool)
                amount = toCool;
        }

        setTemperature(getTemperature() - design.getCoolingAmount() * amount);

        ItemStack newItem = design.getCoolingToolUsed(player.getInventory().getItemInMainHand());
        //remove only one item if the material is AIR else replace the item (e.g. water bucket with a bucket)
        if (newItem.getType().equals(Material.AIR))
            InventoryManagement.takeFromPlayerHand(player, 1);
        else
            player.getInventory().setItemInMainHand(newItem);

        return true;
    }

    /**
     * cools down a cannon by using the item in hand of a player
     *
     * @param player    player using the cannon
     * @param effectLoc location of the smoke effects
     */
    public boolean coolCannon(Player player, Location effectLoc) {
        boolean cooled = coolCannon(player);
        if (cooled && effectLoc != null && getTemperature() > design.getWarningTemperature()) {
            effectLoc.getWorld().playEffect(effectLoc, Effect.SMOKE, BlockFace.UP);
            SoundUtils.playSound(effectLoc, design.getSoundCool());
        }
        return cooled;
    }


    /**
     * return the firing vector of the cannon. The spread depends on the cannon, the projectile and the player
     *
     * @param addSpread       if there is spread added to the firing vector
     * @param usePlayerSpread if additional spread of the player will be added
     * @return firing vector
     */
    public Vector getFiringVector(boolean addSpread, boolean usePlayerSpread) {
        if (firingData.getLastFiredProjectile() == null && ammoLoadingData.getLoadedProjectile() == null)
            return noVelocity.clone();
        Projectile projectile = ammoLoadingData.getLoadedProjectile();
        if (projectile == null)
            projectile = firingData.getLastFiredProjectile();

        double playerSpread = 1.0;
        if (usePlayerSpread)
            playerSpread = getLastPlayerSpreadMultiplier();

        final double spread = design.getSpreadOfCannon() * projectile.getSpreadMultiplier() * playerSpread;
        double deviation = 0.0;

        if (addSpread)
            deviation = random.nextGaussian() * spread;
        double h = (getTotalHorizontalAngle() + deviation + CannonsUtil.directionToYaw(getCannonDirection()));

        if (addSpread)
            deviation = random.nextGaussian() * spread;
        double v = (-getTotalVerticalAngle() + deviation);

        double multi = getCannonballVelocity();
        if (multi < 0.1) multi = 0.1;

        double randomness = 1.0;
        if (addSpread)
            randomness = (1.0 + random.nextGaussian() * spread / 180.0);
        return CannonsUtil.directionToVector(h, v, multi * randomness);
    }

    /**
     * returns the vector the cannon is currently aiming
     *
     * @return vector the cannon is aiming
     */
    public Vector getAimingVector() {
        double multi = Math.max(getCannonballVelocity(), 0.1);
        return CannonsUtil.directionToVector(getTotalHorizontalAngle() + CannonsUtil.directionToYaw(getCannonDirection()), -getTotalVerticalAngle(), multi);
    }

    /**
     * returns the vector the cannon is currently targeting
     *
     * @return targeting vector
     */
    public Vector getTargetVector() {
        double multi = Math.max(getCannonballVelocity(), 0.1);
        return CannonsUtil.directionToVector(getAimingYaw(), getAimingPitch(), multi);
    }

    /**
     * etracts the spreadMultiplier from the permissions
     *
     * @param player player operating the cannon
     * @return spread multiplier
     */
    private double getPlayerSpreadMultiplier(Player player) {
        if (player == null) return 1.0;


        // only if the permissions system is enabled. If there are no permissions, everything is true.
        if (player.hasPermission(this.getCannonDesign().getPermissionSpreadMultiplier() + "." + Integer.MAX_VALUE)) {
            return 1.0;
        }
        // search if there is a valid entry
        for (int i = 1; i <= 10; i++) {
            if (player.hasPermission(this.getCannonDesign().getPermissionSpreadMultiplier() + "." + i)) {
                return i / 10.0;
            }
        }

        //using default value
        return 1.0;
    }

    /**
     * returns the speed of the cannonball depending on the cannon, projectile,
     *
     * @return the velocity of the load projectile, 0 if nothing is loaded
     */
    public double getCannonballVelocity() {
        if ((ammoLoadingData.getLoadedProjectile() == null && firingData.getLastFiredProjectile() == null) || design == null)
            return 0.0;

        int loadableGunpowder = design.getMaxLoadableGunpowderNormal();
        if (loadableGunpowder <= 0)
            loadableGunpowder = 1;

        if (ammoLoadingData.getLoadedProjectile() == null)
            return firingData.getLastFiredProjectile().getVelocity() * design.getMultiplierVelocity() * (1 - Math.pow(2, (double) (-4 * firingData.getLastFiredGunpowder()) / loadableGunpowder));
        else
            return ammoLoadingData.getLoadedProjectile().getVelocity() * design.getMultiplierVelocity() * (1 - Math.pow(2, (double) (-4 * ammoLoadingData.getLoadedGunpowder()) / loadableGunpowder));
    }

    /**
     * @return true if the cannons has a sign
     */
    public boolean hasCannonSign() {
        // search all possible sign locations
        for (Location signLoc : design.getChestsAndSigns(this)) {
            if (signLoc.getBlock().getBlockData() instanceof WallSign)
                return true;
        }
        return false;
    }

    /**
     * @return the number of signs on a cannon
     */
    public int getNumberCannonSigns() {
        // search all possible sign locations
        int i = 0;
        for (Location signLoc : design.getChestsAndSigns(this)) {
            if (signLoc.getBlock().getBlockData() instanceof WallSign)
                i++;
        }
        return i;
    }

    /**
     * returns true if cannon design for this cannon is found
     *
     * @param cannonDesign
     * @return result
     */
    public boolean equals(CannonDesign cannonDesign) {
        return this.sameDesign(cannonDesign);
    }

    /**
     * @param obj - object to compare
     * @return true if both cannons are equal
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ICannon obj2) {
            return this.getUID().equals(obj2.getUID());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mainData.getDatabaseId().hashCode();
    }

    @Override
    public boolean sameType(ICannon cannon) {
        if (!(cannon instanceof Cannon blockCannon)) {
            return false;
        }

        return blockCannon.getCannonDesign().equals(this.design);
    }

    //TODO: Add a limit to these methods here
    @Override
    public int getLoadedGunpowder() {
        if (ammoLoadingData.getLoadedGunpowder() < design.getMaxLoadableGunpowderNormal() && !design.isGunpowderNeeded())
            design.getMaxLoadableGunpowderNormal();

        return ammoLoadingData.getLoadedGunpowder();
    }

    /**
     * returns the maximum horizontal angle, depending if the cannon is on a ship or not
     *
     * @return tun the maximum horizontal angle
     */
    public double getMaxHorizontalAngle() {
        return (isOnShip()) ? design.getMaxHorizontalAngleOnShip() : design.getMaxHorizontalAngleNormal();
    }

    /**
     * returns the minimum horizontal angle, depending if the cannon is on a ship or not
     *
     * @return the minimum horizontal angle
     */
    public double getMinHorizontalAngle() {
        return (isOnShip()) ? design.getMinHorizontalAngleOnShip() : design.getMinHorizontalAngleNormal();
    }

    /**
     * returns the maximum vertical angle, depending if the cannon is on a ship or not
     *
     * @return returns the maximum vertical angle
     */
    public double getMaxVerticalAngle() {
        return (isOnShip()) ? design.getMaxVerticalAngleOnShip() : design.getMaxVerticalAngleNormal();
    }

    /**
     * returns the minimum vertical angle, depending if the cannon is on a ship or not
     *
     * @return returns the minimum vertical angle
     */
    public double getMinVerticalAngle() {
        return (isOnShip()) ? design.getMinVerticalAngleOnShip() : design.getMinVerticalAngleNormal();
    }

    public void setCannonDesign(CannonDesign design) {
        this.design = design;
        this.hasUpdated();
    }

    public CannonDesign getCannonDesign() {
        return this.design;
    }

    public void setLastUser(UUID lastUser) {
        this.firingData.setLastUser(lastUser);
        if (design.isLastUserBecomesOwner())
            this.setOwner(lastUser);
    }

    public boolean isFiring() {
        //check if firing is finished and not reseted (after server restart)
        Projectile projectile = getLoadedProjectile();
        //delayTime is the time how long the firing should take
        long delayTime = (long) (design.getFuseBurnTime() * 1000.);
        if (projectile != null)
            delayTime += (long) (((projectile.getAutomaticFiringMagazineSize() - 1) * projectile.getAutomaticFiringDelay()) * 1000.0);

        return (getLastIgnited() + delayTime) >= System.currentTimeMillis();
    }

    public boolean finishedFiringAndLoading() {
        //check if firing is finished and not reseted (after server restart)
        Projectile projectile = getLoadedProjectile();
        //delayTime is the time how long the firing should take
        long delayTime = (long) ((design.getFuseBurnTime() + design.getLoadTime()) * 1000.);
        if (projectile != null)
            delayTime += (long) (((projectile.getAutomaticFiringMagazineSize() - 1) * projectile.getAutomaticFiringDelay() + design.getLoadTime()) * 1000.0);

        return (getLastIgnited() + delayTime) < System.currentTimeMillis();
    }

    public boolean isLoading() {
        //delayTime is the time how long the loading should take
        long delayTime = (long) (design.getLoadTime() * 1000.0);
        return (getLastLoaded() + delayTime) > System.currentTimeMillis();
    }

    /**
     * returns the ambient temperature for the cannon in celsius
     *
     * @return ambient temperature for the cannon in celsius
     */
    public double getAmbientTemperature() {
        return (Math.sqrt(getMuzzle().getBlock().getTemperature()) - 0.5) * 60;
    }

    /**
     * returns the temperature of the cannon
     *
     * @return cannon temperature
     */
    public double getTemperature() {
        //barrel temperature - minus ambient temperature + exponential decay
        double timePassed = (System.currentTimeMillis() - this.getTemperatureTimeStamp()) / 1000.0;
        double decay = Math.exp(-timePassed / design.getCoolingCoefficient());
        double ambient = getAmbientTemperature();
        double newValue = ambient + (ammoLoadingData.getTempValue() - ambient) * decay;
        ammoLoadingData.setTempValue(newValue);
        setTemperatureTimeStamp(System.currentTimeMillis());

        return newValue;
    }

    /**
     * sets the temperature of the cannon to the given value
     *
     * @param temperature - temperature of the cannon
     */
    public void setTemperature(double temperature) {
        this.setTemperatureTimeStamp(System.currentTimeMillis());
        ammoLoadingData.setTempValue(temperature);
        this.hasUpdated();
    }

    public boolean isOverheated() {
        return getTemperature() > design.getCriticalTemperature();
    }

    /**
     * checks if the cannon will be overheated after firing the loaded charge
     *
     * @return true if the cannon reaches the crital limit
     */
    public boolean isOverheatedAfterFiring() {
        return getTemperature() + design.getHeatIncreasePerGunpowder() * getLoadedGunpowder() > design.getCriticalTemperature();
    }

    /**
     * if the cannon can be fired or if it is too hot
     *
     * @return true if the cannon can be loaded
     */
    public boolean isReadyToFire() {
        return isLoaded() && !isOverheatedAfterFiring() && !isFiring() && isClean() && !barrelTooHot() && isProjectilePushed() && finishedFiringAndLoading();
    }

    /**
     * if the barrel is still cooling down from the last time fired
     *
     * @return true if the barrel it too hot
     */
    public boolean barrelTooHot() {
        return getLastFired() + design.getBarrelCooldownTime() * 1000 >= System.currentTimeMillis();
    }

    /**
     * the total angle is sum of the cannon angle, its design and the location where it is mounted
     *
     * @return sum of all angles
     */
    public double getTotalVerticalAngle() {
        return design.getDefaultVerticalAngle() + this.getVerticalAngle() + this.getAdditionalVerticalAngle();
    }

    /**
     * get the default horizontal home position of the cannon
     *
     * @return default horizontal home position
     */
    public double getHomeHorizontalAngle() {
        return (design.getMaxHorizontalAngleNormal() + design.getMinHorizontalAngleNormal()) / 2.0;
    }

    /**
     * get the default vertical home position of the cannon
     *
     * @return default vertical home position
     */
    public double getHomeVerticalAngle() {
        return (design.getMaxVerticalAngleNormal() + design.getMinVerticalAngleNormal()) / 2.0;
    }

    /**
     * get the default horizontal home position of the cannon
     *
     * @return default horizontal home position
     */
    public double getHomeYaw() {
        return getHomeHorizontalAngle() + CannonsUtil.directionToYaw(getCannonDirection());
    }

    /**
     * get the default vertical home position of the cannon
     *
     * @return default vertical home position
     */
    public double getHomePitch() {
        return getHomeVerticalAngle();
    }

    /**
     * if the cannon has the target in sight and angles are set correctly
     *
     * @return true if aiminig is finished
     */
    public boolean targetInSight() {
        return Math.abs(getAimingYaw() - getHorizontalYaw()) < design.getAngleStepSize() && Math.abs(getAimingPitch() - getVerticalPitch()) < design.getAngleStepSize();
    }

    /**
     * whenever the cannon can aim in this direction or not
     *
     * @param yaw horizontal angle
     * @return true if it can aim this direction
     */
    public boolean canAimYaw(double yaw) {
        double horizontal = yaw - CannonsUtil.directionToYaw(getCannonDirection()) - this.getAdditionalHorizontalAngle();

        horizontal = horizontal % 360;
        while (horizontal < -180)
            horizontal = horizontal + 360;
        return (horizontal > getMinHorizontalAngle() && horizontal < getMaxHorizontalAngle());
    }

    /**
     * whenever the cannon can aim in this direction or not
     *
     * @param pitch vertical angle
     * @return true if it can aim this direction
     */
    public boolean canAimPitch(double pitch) {
        double vertical = -pitch - design.getDefaultVerticalAngle() - this.getAdditionalVerticalAngle();
        return (vertical > getMinVerticalAngle() && vertical < getMaxVerticalAngle());
    }

    public double verticalAngleToPitch(double vertical) {
        return -vertical - design.getDefaultVerticalAngle() - this.getAdditionalVerticalAngle();
    }

    public double horizontalAngleToYaw(double horizontal) {
        double yaw = horizontal + this.getHorizontalAngle() + CannonsUtil.directionToYaw(getCannonDirection());

        yaw %= 360;
        while (yaw < -180)
            yaw += 360;
        while (yaw > 180)
            yaw -= 360;
        return yaw;
    }

    @Override
    public HashMap<UUID, Boolean> getObserverMap() {
        return observerMap;
    }

    /**
     * add the player as observer for this cannon
     *
     * @param player             player will be added as observer
     * @param removeAfterShowing if true, the observer only works once
     * @return message for the player
     */
    public MessageEnum addObserver(Player player, boolean removeAfterShowing) {
        Validate.notNull(player, "player must not be null");

        //permission check
        if (!player.hasPermission(design.getPermissionObserver()))
            return MessageEnum.PermissionErrorObserver;

        //the player might have an entry which allows unlimited observing (e.g. observer)
        //removeAfterShowing == true is weaker
        if (observerMap.get(player.getUniqueId()) != Boolean.FALSE)
            observerMap.put(player.getUniqueId(), removeAfterShowing);
        return MessageEnum.CannonObserverAdded;
    }

    /**
     * gets the chance of cannon explosion due to overloading of gunpowder
     *
     * @return chance of explosion
     */
    public double getOverloadingExplosionChance() {
        if (!design.isOverloadingEnabled()) {
            return 0.0;
        }

        double tempInc;
        if (design.isOverloadingDependsOfTemperature())
            tempInc = ammoLoadingData.getTempValue() / design.getMaximumTemperature();
        else
            tempInc = 1;

        double chance = getChance(tempInc);
        return (chance <= 0) ? 0.0 : chance;
    }

    private double getChance(double tempInc) {
        int saferGunpowder;
        if (design.isOverloadingRealMode())
            saferGunpowder = 0;
        else
            saferGunpowder = design.getMaxLoadableGunpowderNormal();

        //prevent negative values
        int gunpowder = ammoLoadingData.getLoadedGunpowder() - saferGunpowder;
        if (gunpowder < 0)
            gunpowder = 0;
        return tempInc * design.getOverloadingChangeInc() * Math.pow(gunpowder * design.getOverloadingChanceOfExplosionPerGunpowder(), design.getOverloadingExponent());
    }

    /**
     * Calculating if cannon might to explode
     *
     * @return true if explosion chance was more then random number
     */
    public boolean isExplodedDueOverloading() {
        double chance = getOverloadingExplosionChance();
        //Cannons.getPlugin().logDebug("Chance of explosion (overloading) = " + design.getOverloadingChangeInc() + " * ((" + loadedGunpowder + " ( may to be - " + design.getMaxLoadableGunpowder_Normal() + ")) * " + design.getOverloadingChanceOfExplosionPerGunpowder() + ") ^ " + design.getOverloadingExponent() + " (may to be multiplied by " + tempValue + " / " + design.getMaximumTemperature() + " = " + chance);
        return Math.random() < chance;
    }

    public double getLastPlayerSpreadMultiplier() {
        return lastPlayerSpreadMultiplier;
    }

    public void setLastPlayerSpreadMultiplier(Player player) {
        this.lastPlayerSpreadMultiplier = getPlayerSpreadMultiplier(player);
    }

    public void resetLastPlayerSpreadMultiplier() {
        this.lastPlayerSpreadMultiplier = 1.0;
    }

    public boolean isChunkLoaded() {
        Chunk chunk = getLocation().getChunk();
        return chunk.isLoaded();
    }

    @Override
    public WhitelistData getWhitelistData() {
        return whitelistData;
    }

    @Override
    public void setWhitelistData(WhitelistData data) {
        hasWhitelistUpdated();
        hasUpdated();
        whitelistData = data;
    }

    public void removeWhitelistPlayer(UUID playerUID) {
        if (playerUID == getOwner()) {
            Cannons.getPlugin().logDebug("can't remove Owner from Whitelist");
            return;
        }
        setLastWhitelisted(playerUID);
        whitelistData.getWhitelist().remove(playerUID);
        this.hasWhitelistUpdated();
    }

    /**
     * returns true if player is allows to operated a cannon (whitelisted or owner)
     *
     * @param playerUID player ID to test
     * @return
     */
    public boolean isOperator(UUID playerUID) {
        return (isWhitelisted(playerUID) || playerUID == getOwner());
    }

    public void boughtByPlayer(UUID playerID) {
        setPaid(true);
        setOwner(playerID);
        whitelistData.clear();
        whitelistData.add(playerID);
    }

    public EntityType getProjectileEntityType() {
        if (ammoLoadingData.getLoadedProjectile() != null) {
            return ammoLoadingData.getLoadedProjectile().getProjectileEntity();
        }
        if (firingData.getLastFiredProjectile() != null) {
            return firingData.getLastFiredProjectile().getProjectileEntity();
        }
        return EntityType.SNOWBALL;
    }

    /**
     * add the player as cannon operator for this cannon, if
     *
     * @param player       player will be added as cannon operator
     * @param masterCannon if the controlled cannon is a slave and not the master cannon
     * @return message for the player
     */
    public MessageEnum addCannonOperator(Player player, Boolean masterCannon) {
        Validate.notNull(player, "player must not be null");

        //permission check
        if (!player.hasPermission(design.getPermissionAutoaim()))
            return MessageEnum.PermissionErrorAutoaim;

        //check if only the owner can control the cannon
        if (this.getCannonDesign().isAccessForOwnerOnly() && this.getOwner() != player.getUniqueId())
            return MessageEnum.ErrorNotTheOwner;

        //there might already be a player controlling the cannon
        this.setCannonOperator(player.getUniqueId());
        this.setMasterCannon(masterCannon);
        return MessageEnum.AimingModeEnabled;
    }

    @Override
    public FiringData getFiringData() {
        this.hasUpdated();
        return this.firingData;
    }

    @Override
    public void setFiringData(FiringData firingData) {
        this.hasUpdated();
        this.firingData = firingData;
    }

    @Override
    public AimingData getAimingData() {
        this.hasUpdated();
        return this.aimingData;
    }

    @Override
    public void setAimingData(AimingData aimingData) {
        this.hasUpdated();
        this.aimingData = aimingData;
    }

    @Override
    public SentryData getSentryData() {
        this.hasUpdated();
        return this.sentryData;
    }

    @Override
    public void setSentryData(SentryData sentryData) {
        this.hasUpdated();
        this.sentryData = sentryData;
    }

    @Override
    public CannonPosition getCannonPosition() {
        this.hasUpdated();
        return cannonPosition;
    }

    @Override
    public void setCannonPosition(CannonPosition position) {
        this.hasUpdated();
        this.cannonPosition = position;        
    }

    @Override
    public CannonMainData getCannonMainData() {
        this.hasUpdated();
        return mainData;
    }

    @Override
    public void setCannonMainData(CannonMainData data) {
        this.hasUpdated();
        this.mainData = data;
    }

    @Override
    public LinkingData getLinkingData() {
        this.hasUpdated();
        return linkingData;
    }

    @Override
    public void setLinkingData(LinkingData data) {
        this.hasUpdated();
        this.linkingData = data;
    }

    @Override
    public AngleData getAngleData() {
        this.hasUpdated();
        return angleData;
    }

    @Override
    public void setAngleData(AngleData angleData) {
        this.hasUpdated();
        this.angleData = angleData;
    }

    @Override
    public AmmoLoadingData getAmmoLoadingData() {
        this.hasUpdated();
        return ammoLoadingData;
    }

    @Override
    public void setAmmoLoadingData(AmmoLoadingData ammoLoadingData) {
        this.hasUpdated();
        this.ammoLoadingData = ammoLoadingData;
    }
}
