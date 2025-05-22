package at.pavlov.cannons;

import at.pavlov.cannons.Enum.InteractAction;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.Enum.ProjectileCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.cannons.dao.wrappers.BaseFireTask;
import at.pavlov.cannons.event.CannonFireEvent;
import at.pavlov.cannons.event.CannonLinkFiringEvent;
import at.pavlov.cannons.event.CannonUseEvent;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class FireCannon {

    private final Config config;
    private final Cannons plugin;

    private final Random random = new Random();


    public FireCannon(Cannons plugin) {
        this.plugin = plugin;
        this.config = Config.getInstance();
    }


    /**
     * checks all condition but does not fire the cannon
     *
     * @param cannon cannon which is fired
     * @param player operator of the cannon
     * @return message for the player
     */
    private MessageEnum getPrepareFireMessage(Cannon cannon, Player player) {
        CannonDesign design = cannon.getCannonDesign();
        if (design == null) return null;
        //if the player is not the owner of this gun
        if (player != null && cannon.getOwner() != null && !cannon.getOwner().equals(player.getUniqueId()) && design.isAccessForOwnerOnly())
            return MessageEnum.ErrorNotTheOwner;
        //Loading in progress
        if (cannon.isLoading() && !design.isFireAfterLoading())
            return MessageEnum.ErrorLoadingInProgress;
        // is the cannon already cleaned?
        if (!cannon.isClean())
            return MessageEnum.ErrorNotCleaned;
        //check if there is some gunpowder in the barrel
        if (!cannon.isGunpowderLoaded())
            return MessageEnum.ErrorNoGunpowder;//is there a projectile
        // is there a projectile in the cannon?
        if (!cannon.isLoaded() && !design.isFireAfterLoading())
            return MessageEnum.ErrorNoProjectile;
        // after cleaning the projectile needs to pushed in the barrel
        if (!cannon.isProjectilePushed())
            return MessageEnum.ErrorNotPushed;
        //Firing in progress
        if (cannon.isFiring())
            return MessageEnum.ErrorFiringInProgress;
        // fee not paid
        if (!cannon.isPaid())
            return MessageEnum.ErrorNotPaid;
        //Barrel too hot
        if (cannon.barrelTooHot())
            return MessageEnum.ErrorBarrelTooHot;
        //automatic temperature control, prevents overheating of the cannon
        if (design.isAutomaticTemperatureControl() && cannon.isOverheatedAfterFiring())
            return MessageEnum.ErrorBarrelTooHot;

        if (player == null) {
            return MessageEnum.CannonFire;
        }
        //if the player has permission to fire
        if (!player.hasPermission(design.getPermissionFire()))
            return MessageEnum.PermissionErrorFire;

        //check if the player has the permission for this projectile
//            Projectile projectile = cannon.getLoadedProjectile();
//            if(projectile != null && !projectile.hasPermission(player))
//                return MessageEnum.PermissionErrorProjectile;
        //check for flint and steel
        if (design.isFiringItemRequired() && !config.getToolFiring().equalsFuzzy(player.getInventory().getItemInMainHand()))
            return MessageEnum.ErrorNoFlintAndSteel;
        //everything fine fire the damn cannon
        return MessageEnum.CannonFire;
    }

    /**
     * checks if all preconditions for firing are fulfilled and fires the cannon
     * Default fire event for players
     *
     * @param cannon - cannon to fire
     * @param action - how has the player/plugin interacted with the cannon
     * @return - message for the player
     */
    public MessageEnum redstoneFiring(Cannon cannon, InteractAction action) {
        CannonDesign design = cannon.getCannonDesign();
        return this.fire(cannon, null, cannon.getCannonDesign().isAutoreloadRedstone(), !design.isAmmoInfiniteForRedstone(), action);
    }

    /**
     * checks if all preconditions for firing are fulfilled and fires the cannon
     * Default fire event for players
     *
     * @param cannon - cannon to fire
     * @param player - operator of the cannon
     * @param action - how has the player/plugin interacted with the cannon
     * @return - message for the player
     */
    public MessageEnum playerFiring(Cannon cannon, Player player, InteractAction action) {
        CannonDesign design = cannon.getCannonDesign();
        boolean autoreload = player.isSneaking() && player.hasPermission(design.getPermissionAutoreload());

        //todo add firing of multiple cannons
        if (!design.isLinkCannonsEnabled()) {
            return this.fire(cannon, player.getUniqueId(), autoreload, !design.isAmmoInfiniteForPlayer(), action);
        }

        int d = design.getLinkCannonsDistance() * 2;

        LinkedList<Cannon> linkedCannons = new LinkedList<>();
        for (Cannon fcannon : CannonManager.getCannonsInBox(cannon.getLocation(), d, d, d)) {
            plugin.logDebug(fcannon.getCannonName() + " is cannon operator: " + fcannon.isCannonOperator(player));
            boolean allowedToFire = cannon.isAccessLinkingAllowed(fcannon, player);

            if (fcannon.isCannonOperator(player) &&
                    fcannon.canAimYaw(player.getEyeLocation().getYaw()) &&
                    /*fcannon.isAimingFinished() &&*/
                    fcannon.sameDesign(cannon) &&
                    allowedToFire) {
                linkedCannons.add(fcannon);
            }
        }

        CannonLinkFiringEvent event = new CannonLinkFiringEvent(cannon, linkedCannons, player.getUniqueId());
        Bukkit.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            for (var fcannon : event.getLinkedCannons()) {
                this.fire(fcannon, player.getUniqueId(), autoreload, !design.isAmmoInfiniteForPlayer(), action);
            }
        }

        return this.fire(cannon, player.getUniqueId(), autoreload, !design.isAmmoInfiniteForPlayer(), action);
    }

    /**
     * checks if all preconditions for firing are fulfilled and fires the cannon
     * Default fire event for players
     *
     * @param cannon - cannon to fire
     * @return - message for the player
     */
    public MessageEnum sentryFiring(Cannon cannon) {
        CannonDesign design = cannon.getCannonDesign();

        return this.fire(cannon, null, true, !design.isAmmoInfiniteForPlayer(), InteractAction.fireSentry);
    }

    /**
     * checks if all preconditions for firing are fulfilled and fires the cannon
     *
     * @param cannon       the cannon which is fired
     * @param playerUid    player operating the cannon
     * @param autoload     the cannon will autoreload before firing
     * @param consumesAmmo if true ammo will be removed from chest inventories
     * @return message for the player
     */
    public MessageEnum fire(Cannon cannon, UUID playerUid, boolean autoload, boolean consumesAmmo, InteractAction action) {
        plugin.logDebug("fire cannon");
        //set some valid shooter is none is given
        if (playerUid == null) {
            playerUid = cannon.getOwner();
        }
        Player player = Bukkit.getPlayer(playerUid);
        //fire event
        CannonUseEvent useEvent = new CannonUseEvent(cannon, playerUid, action);
        Bukkit.getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled())
            return null;

        CannonDesign design = cannon.getCannonDesign();

        //if there is no gunpowder needed we set it to the maximum
        if (!design.isGunpowderNeeded() && cannon.getLoadedGunpowder() == 0)
            cannon.setLoadedGunpowder(design.getMaxLoadableGunpowderNormal());


        MessageEnum messageEnum = tryAutoload(cannon, playerUid, autoload, consumesAmmo, design);
        if (messageEnum != null) return messageEnum;

        //check for all permissions
        MessageEnum message = getPrepareFireMessage(cannon, player);

        if (message != null && message.isError())
            SoundUtils.playErrorSound(cannon.getMuzzle());

        //return if there are permission missing
        if (message != MessageEnum.CannonFire)
            return message;

        CannonFireEvent fireEvent = new CannonFireEvent(cannon, playerUid);
        Bukkit.getServer().getPluginManager().callEvent(fireEvent);

        if (fireEvent.isCancelled())
            return null;

        Projectile projectile = cannon.getLoadedProjectile();
        //reset after firing
        cannon.setLastFired(System.currentTimeMillis());
        //this cannon is now firing
        cannon.setFiring();
        //store spread of cannon operator
        cannon.setLastPlayerSpreadMultiplier(player);

        //Set up smoke effects on the torch
        for (Location torchLoc : design.getFiringIndicator(cannon)) {
            torchLoc.setX(torchLoc.getX() + 0.5);
            torchLoc.setY(torchLoc.getY() + 1);
            torchLoc.setZ(torchLoc.getZ() + 0.5);
            torchLoc.getWorld().playEffect(torchLoc, Effect.SMOKE, BlockFace.UP);
            SoundUtils.playSound(torchLoc, design.getSoundIgnite());
        }

        final ProjectileCause projectileCause = switch (action) {
            case fireRightClickTigger, fireAutoaim, fireRedstoneTrigger, fireAfterLoading -> ProjectileCause.PlayerFired;
            case fireRedstone -> ProjectileCause.RedstoneFired;
            case fireSentry -> ProjectileCause.SentryFired;
            default -> ProjectileCause.UnknownFired;
        };

        var scheduler = AsyncTaskManager.get().scheduler;
        try {
            //set up delayed task with automatic firing. Several bullets with time delay for one loaded projectile
            for (int i = 0; i < projectile.getAutomaticFiringMagazineSize(); i++) {
                //charge is only removed in the last round fired
                boolean lastRound = i == (projectile.getAutomaticFiringMagazineSize() - 1);
                double randomess = 1. + design.getFuseBurnTimeRandomness() * random.nextDouble();
                long delayTime = (long) (randomess * design.getFuseBurnTime() * 20.0 + i * projectile.getAutomaticFiringDelay() * 20.0);
                BaseFireTask fireTaskWrapper = fireEvent.getFireTaskCreator().create(cannon, playerUid, lastRound, projectileCause);
                scheduler.runTaskLater(cannon.getLocation(), fireTaskWrapper::fireTask, delayTime);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    private @Nullable MessageEnum tryAutoload(Cannon cannon, UUID playerUid, boolean autoload, boolean consumesAmmo, CannonDesign design) {
        //no charge try some autoreload from chests
        if (cannon.isLoaded() || cannon.isLoading() || !autoload) {
            return null;
        }

        MessageEnum messageEnum = cannon.reloadFromChests(playerUid, consumesAmmo);
        //try to load some projectiles
        if (messageEnum.isError()) {
            //there is not enough gunpowder or no projectile in the chest
            plugin.logDebug("Can't reload cannon, because there is no valid charge in the chests");
            SoundUtils.playErrorSound(cannon.getMuzzle());
            return messageEnum;
        }
        //everything went fine - next click on torch will fire the cannon
        //if fire after reloading is active, it will fire automatically. This can be a problem for the impact predictor
        return design.isFireAfterLoading() ? null : MessageEnum.loadProjectile;
    }
}
