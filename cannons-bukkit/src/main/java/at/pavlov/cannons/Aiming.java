package at.pavlov.cannons;

import at.pavlov.cannons.Enum.FakeBlockType;
import at.pavlov.cannons.Enum.InteractAction;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.Enum.TargetType;
import at.pavlov.cannons.aim.GunAngles;
import at.pavlov.cannons.aim.GunAnglesWrapper;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.config.UserMessages;
import at.pavlov.cannons.container.MovingObject;
import at.pavlov.cannons.container.Target;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.cannons.event.CannonLinkAimingEvent;
import at.pavlov.cannons.event.CannonTargetEvent;
import at.pavlov.cannons.event.CannonUseEvent;
import at.pavlov.cannons.hooks.towny.TownyHook;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.scheduler.FakeBlockHandler;
import at.pavlov.cannons.utils.CannonsUtil;
import at.pavlov.cannons.utils.SoundUtils;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.utils.CombatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class Aiming {

    private final Cannons plugin;
    private final UserMessages userMessages;
    private final Config config;

    //<Player,cannon name>
    private final HashMap<UUID, UUID> inAimingMode = new HashMap<>();
    //<Cannon>
    private final HashSet<UUID> sentryCannons = new HashSet<>();
    //<Player>
    private final HashSet<UUID> imitatedEffectsOff = new HashSet<>();

    //<cannon uniqueId, timespamp>
    private final HashMap<UUID, Long> lastAimed = new HashMap<>();

    private final Random random = new Random();

    @Getter
    private static Aiming instance = null;

    private Aiming(Cannons plugin) {
        this.plugin = plugin;
        this.config = plugin.getMyConfig();
        this.userMessages = UserMessages.getInstance();
    }

    public static void initialize(Cannons plugin) {
        if (instance != null) {
            return;
        }

        instance = new Aiming(plugin);
    }

    private Scoreboard getScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null)
            return null;

        return manager.getMainScoreboard();
    }

    /**
     * starts the aiming mode scheduler
     */
    public void initAimingMode() {
        //changing angles for aiming mode
        AsyncTaskManager.get().scheduler.runTaskTimer(() -> {
            long startTime = System.nanoTime();
            updateAimingMode();
            double time = (System.nanoTime() - startTime) / 1000000.0;
            if (time > 10.)
                plugin.logDebug("Time update aiming: " + new DecimalFormat("0.00").format(time) + "ms");

            startTime = System.nanoTime();
            updateImpactPredictor();
            time = (System.nanoTime() - startTime) / 1000000.0;
            if (time > 10.)
                plugin.logDebug("Time updateImpactPredictor: " + new DecimalFormat("0.00").format(time) + "ms");

            startTime = System.nanoTime();
            updateSentryMode();
            time = (System.nanoTime() - startTime) / 1000000.0;
            if (time > 10.)
                plugin.logDebug("Time updateSentryMode: " + new DecimalFormat("0.00").format(time) + "ms");

        }, 1L, 1L);
    }

    /**
     * player click interaction with cannon
     *
     * @param cannon      operated cannon
     * @param action      interaction of player with cannon
     * @param clickedFace which side was clicked (up, down, left, right)
     * @param player      operator of the cannon
     * @return message for the player
     */
    public MessageEnum changeAngle(Cannon cannon, Action action, BlockFace clickedFace, Player player) {
        //fire event
        CannonUseEvent useEvent = new CannonUseEvent(cannon, player.getUniqueId(), InteractAction.adjustPlayer);
        Bukkit.getServer().getPluginManager().callEvent(useEvent);

        if (useEvent.isCancelled())
            return null;


        if (!action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return null;
        }

        if (!config.getToolAutoaim().equalsFuzzy(player.getInventory().getItemInMainHand())) {
            //barrel clicked to change angle
            return updateAngle(player, cannon, clickedFace, InteractAction.adjustPlayer);
        }
        //aiming mode
        aimingMode(player, cannon, false);
        return null;
    }

    /**
     * evaluates the new cannon angle and returns a message for the user
     *
     * @param player      operator of the cannon
     * @param cannon      operated cannon
     * @param clickedFace which side was clicked (up, down, left, right)
     * @return message for the player
     */
    private MessageEnum updateAngle(Player player, Cannon cannon, BlockFace clickedFace, InteractAction action) {
        if (cannon == null)
            return null;

        CannonDesign design = cannon.getCannonDesign();
        boolean isSentry = design.isSentry();

        //both horizontal and vertical angle will be displayed in one message
        MessageEnum message = null;

        MessageEnum resultPerm = checkPermissions(player, cannon);
        if (resultPerm != null)
            return resultPerm;

        GunAnglesWrapper gunAnglesWrapper = determineGunAngles(player, cannon, clickedFace, action, isSentry);
        if (gunAnglesWrapper.angles == null) return null;

        MessageEnum output = adjustAngles(gunAnglesWrapper, cannon);
        boolean hasChanged = output != null;
        if (hasChanged)
            message = output;

        //update the time
        cannon.setLastAimed(System.currentTimeMillis());
        //show aiming vector in front of the cannon
        showAimingVector(cannon, player);

        //display message only if the angle has changed
        if (hasChanged) {
            SoundUtils.playSound(cannon.getMuzzle(), design.getSoundAdjust());
            //predict impact marker
            updateLastAimed(cannon);
            if (design.isAngleUpdateMessage())
                return message;

            return null;
        }

        //set homing finished flag
        if (isSentry)
            cannon.setSentryHomedAfterFiring(true);
        //no change in angle
        return null;
    }

    private MessageEnum checkPermissions(Player player, Cannon cannon) {

        if (player == null)
            return null;

        UUID owner = cannon.getOwner();
        CannonDesign design = cannon.getCannonDesign();
        //if the player is not the owner of this gun
        if (owner != null && !owner.equals(player.getUniqueId()) && design.isAccessForOwnerOnly())
            return MessageEnum.ErrorNotTheOwner;
        //if the player has the permission to adjust this gun
        if (!player.hasPermission(design.getPermissionAdjust()))
            return MessageEnum.PermissionErrorAdjust;

        return null;
    }

    private MessageEnum adjustAngles(GunAnglesWrapper wrapper, Cannon cannon) {
        CannonDesign design = cannon.getCannonDesign();
        boolean largeChange = false;
        MessageEnum message = null;

        GunAngles angles = wrapper.angles;
        boolean combine = wrapper.combine;

        double absHorizontal = angles.getAbsHorizontal();
        double absVertical = angles.getAbsVertical();

        final double angleStepSize = design.getAngleStepSize();
        final double angleLargeStepSize = design.getAngleLargeStepSize();
        if (absHorizontal >= angleLargeStepSize && setHorizontalAngle(cannon, angles, angleLargeStepSize)) {
            largeChange = true;
            message = setMessageHorizontal(cannon, combine);
        }
        //small step if no large step was possible
        if (!largeChange && absHorizontal >= angleStepSize / 2. && setHorizontalAngle(cannon, angles, angleStepSize)) {
            message = setMessageHorizontal(cannon, combine);
        }
        //larger step
        largeChange = false;
        if (absVertical >= angleLargeStepSize && setVerticalAngle(cannon, angles, angleLargeStepSize)) {
            largeChange = true;
            message = setMessageVertical(cannon, combine);
        }
        //small step if no large step was possible
        if (!largeChange && absVertical >= angleStepSize / 2. && setVerticalAngle(cannon, angles, angleStepSize)) {
            message = setMessageVertical(cannon, combine);
        }

        return message;
    }

    public boolean setHorizontalAngle(Cannon cannon, GunAngles angles, double step) {
        step = Math.abs(step);

        final double minHoriz = cannon.getMinHorizontalAngle();
        final double maxHoriz = cannon.getMaxHorizontalAngle();

        if (angles.getHorizontal() >= 0) {
            // right
            if (cannon.getHorizontalAngle() + step <= maxHoriz + 0.001) {
                //if smaller than minimum -> set to minimum
                if (cannon.getHorizontalAngle() < minHoriz)
                    cannon.setHorizontalAngle(minHoriz);

                cannon.setHorizontalAngle(cannon.getHorizontalAngle() + step);
                return true;
            }
        } else if (cannon.getHorizontalAngle() - step >= minHoriz - 0.001) { //left
            //if smaller than maximum -> set to maximum
            if (cannon.getHorizontalAngle() > maxHoriz)
                cannon.setHorizontalAngle(maxHoriz);

            cannon.setHorizontalAngle(cannon.getHorizontalAngle() - step);
            return true;
        }
        return false;
    }

    public boolean setVerticalAngle(Cannon cannon, GunAngles angles, double step) {
        step = Math.abs(step);

        final double minVert = cannon.getMinVerticalAngle();
        final double maxVert = cannon.getMaxVerticalAngle();

        if (angles.getVertical() >= 0.0) {
            // up
            if (cannon.getVerticalAngle() + step <= maxVert + 0.001) {
                //if smaller than minimum -> set to minimum
                if (cannon.getVerticalAngle() < minVert)
                    cannon.setVerticalAngle(minVert);
                cannon.setVerticalAngle(cannon.getVerticalAngle() + step);
                return true;

            }
        } else {
            // down
            if (cannon.getVerticalAngle() - step >= minVert - 0.001) {
                if (cannon.getVerticalAngle() > maxVert)
                    cannon.setVerticalAngle(maxVert);
                cannon.setVerticalAngle(cannon.getVerticalAngle() - step);
                return true;
            }
        }
        return false;
    }


    private GunAnglesWrapper determineGunAngles(Player player, Cannon cannon, BlockFace clickedFace, InteractAction action, boolean isSentry) {
        Location cannonLoc = cannon.getLocation();
        Location playerLoc = null;

        if (player != null) {
            playerLoc = player.getLocation();
        }

        CannonDesign design = cannon.getCannonDesign();

        if (action == InteractAction.adjustSentry && isSentry) {
            if (cannon.isChunkLoaded())
                return new GunAnglesWrapper(GunAngles.getGunAngle(cannon, cannon.getAimingYaw(), cannon.getAimingPitch()), true);

            plugin.logDebug("chunk not loaded. ignore cannon: " + cannonLoc);
            return new GunAnglesWrapper(null, false);
        }

        if (player != null && action == InteractAction.adjustAutoaim && inAimingMode.containsKey(player.getUniqueId())) {
            if (!player.isSneaking()) {
                return new GunAnglesWrapper(null, false);
            }

            GunAngles angles = GunAngles.getGunAngle(cannon, playerLoc.getYaw(), playerLoc.getPitch());
            cannon.setAimingFinished(angles.getAbsHorizontal() < design.getAngleStepSize() && angles.getAbsVertical() < design.getAngleStepSize());
            return new GunAnglesWrapper(angles, true);
        }

        if (isSentry && cannon.isSentryAutomatic())
            return new GunAnglesWrapper(null, false);

        if (player == null)
            return new GunAnglesWrapper(null, false);

        GunAngles angles = CheckBlockFace(clickedFace, cannon.getCannonDirection(), player.isSneaking(), design.getAngleStepSize());
        cannon.addObserver(player, true);
        return new GunAnglesWrapper(angles, false);
    }


    /**
     * returns the angle to change by the given block face
     * 0 - right
     * 1 - left
     * 2 - up
     * 3 - down
     *
     * @param clickedFace     - click block face on the cannon
     * @param cannonDirection - direction the cannon is facing
     * @param isSneaking      - is the player sneaking (will revert all options)
     * @return - angle to change
     */
    private GunAngles CheckBlockFace(BlockFace clickedFace, BlockFace cannonDirection, boolean isSneaking, double step) {
        if (clickedFace == null || cannonDirection == null)
            return new GunAngles(0.0, 0.0);

        double horizontal = 0.0, vertical = 0.0;
        BlockFace rightFace = CannonsUtil.roatateFace(cannonDirection);

        //check up or down
        if (clickedFace.equals(BlockFace.DOWN)) {
            vertical = isSneaking ? step : -step;
        } else if (clickedFace.equals(BlockFace.UP)) {
            vertical = isSneaking ? -step : step;
        } else if (clickedFace.equals(rightFace.getOppositeFace())) {  //check left
            horizontal = isSneaking ? step : -step;
        } else if (clickedFace.equals(rightFace)) {    //check right
            horizontal = isSneaking ? -step : step;
        } else if (clickedFace.equals(cannonDirection) || clickedFace.equals(cannonDirection.getOppositeFace())) {  //check front or back
            //Same as face UP, here for better readability
            vertical = isSneaking ? -step : step;
        }

        return new GunAngles(horizontal, vertical);
    }

    /**
     * returns the cannon of the player if he is in aiming mode
     *
     * @param player the player who is in aiming mode
     * @return the cannon which is in aiming mode by the given player
     */
    public Cannon getCannonInAimingMode(Player player) {
        if (player == null)
            return null;
        //return the cannon of the player if he is in aiming mode
        return getCannonInAimingMode(player.getUniqueId());
    }

    /**
     * returns the cannon of the player if he is in aiming mode
     *
     * @param player the player who is in aiming mode
     * @return the cannon which is in aiming mode by the given player
     */
    public Cannon getCannonInAimingMode(UUID player) {
        if (player == null)
            return null;
        //return the cannon of the player if he is in aiming mode
        return CannonManager.getCannon(inAimingMode.get(player));
    }


    /**
     * if the player is not near the cannon
     *
     * @param player The player which has moved
     * @return false if the player is too far away
     */
    public boolean distanceCheck(Player player, Cannon cannon) {
        // no cannon? then exit
        if (cannon == null)
            return true;

        if (!player.getWorld().equals(cannon.getWorldBukkit()))
            return false;

        //check if player is far away from the cannon
        CannonDesign design = DesignStorage.getInstance().getDesign(cannon);
        //go to trigger location
        Location locCannon = design.getFiringTrigger(cannon);
        //if there is no trigger - set the muzzle a location
        if (locCannon == null)
            locCannon = cannon.getMuzzle();
        if (cannon.getMuzzle() == null) {
            plugin.logSevere("cannon design " + cannon.getCannonDesign() + " has no muzzle location");
            return false;
        }

        return player.getLocation().distanceSquared(locCannon) <= config.getToolAutoaimRange() * config.getToolAutoaimRange();
    }


    /**
     * updates the auto Aiming direction for player in auto-aiming mode
     */
    private void updateAimingMode() {
        //player in map change the angle to the angle the player is looking
        Iterator<Map.Entry<UUID, UUID>> iter = inAimingMode.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, UUID> entry = iter.next();
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player == null) {
                iter.remove();
                continue;
            }


            //find the cannon with this id
            Cannon cannon = CannonManager.getCannon(entry.getValue());
            if (cannon == null) {
                iter.remove();
                continue;
            }

            // only update if since the last update some ticks have past (updateSpeed is in ticks = 50ms)
            if (System.currentTimeMillis() < cannon.getLastAimed() + cannon.getCannonDesign().getAngleUpdateSpeed()) {
                continue;
            }

            boolean playerInRange = distanceCheck(player, cannon);
            // reset diasble aiming mode timer if player is close to the cannon
            if (playerInRange)
                cannon.setTimestampAimingMode(System.currentTimeMillis());

            // autoaming or fineadjusting
            if (handleAutoamingFineadjusting(playerInRange, player, cannon))
                return;

            //leave aiming Mode but wait a second first
            if ((System.currentTimeMillis() - cannon.getTimestampAimingMode()) > 1000) {
                userMessages.sendMessage(MessageEnum.AimingModeTooFarAway, player);
                MessageEnum message = disableAimingMode(player);
                userMessages.sendMessage(message, player, cannon);
            }

        }
    }

    private boolean handleAutoamingFineadjusting(boolean playerInRange, Player player, Cannon cannon) {
        if (!playerInRange || !player.isOnline() || !cannon.isValid() || cannon.getCannonDesign().isSentry() && cannon.isSentryAutomatic()) {
            return false;
        }

        MessageEnum message = updateAngle(player, cannon, null, InteractAction.adjustAutoaim);

        // todo updated cannon angles for linked cannons
        // linked Cannons
        if (!cannon.getCannonDesign().isLinkCannonsEnabled()) {
            userMessages.sendMessage(message, player, cannon);
            return true;
        }

        int d = cannon.getCannonDesign().getLinkCannonsDistance() * 2;
        for (Cannon fcannon : CannonManager.getCannonsInBox(cannon.getLocation(), d, d, d)) {
            // if the design is the same, and the player is allowed to use the cannon
            boolean checkDesign = fcannon.getCannonDesign().equals(cannon.getCannonDesign());
            boolean canAccess = cannon.isAccessLinkingAllowed(fcannon, player);

            if (fcannon.isCannonOperator(player) && checkDesign && canAccess)
                updateAngle(player, fcannon, null, InteractAction.adjustAutoaim);
        }

        userMessages.sendMessage(message, player, cannon);
        return true;
    }

    private void updateSentryMode() {
        Iterator<UUID> iter = sentryCannons.iterator();
        while (iter.hasNext()) {
            Cannon cannon = CannonManager.getCannon(iter.next());
            if (cannon == null) {
                //this cannon does not exist
                iter.remove();
                continue;
            }

            //todo test if chunk loading is caused by this function
            if (!cannon.isChunkLoaded()) {
                plugin.logDebug("Chunk not loaded " + cannon.getCannonName() + " sentry function deactivated");
                continue;
            }

            // ignore cannons which are not in sentry mode
            if (!cannon.isSentryAutomatic() || !cannon.isPaid())
                return;

            sentryLoadChest(cannon);

            calculateFiringSolution(cannon);

            sentryAiming(cannon);
            //ready to fire. Fire!
            sentryFiring(cannon);

            //no targets found, return to default angles
            if (!cannon.hasSentryEntity()) {
                cannon.setAimingYaw(cannon.getHomeYaw());
                cannon.setAimingPitch(cannon.getHomePitch());
                cannon.setSentryHomedAfterFiring(false);
            }
        }
    }

    private void sentryAiming(Cannon cannon) {
        //aim at the found solution
        // only update if since the last update some ticks have past (updateSpeed is in ticks = 50ms)
        if (System.currentTimeMillis() < cannon.getLastAimed() + cannon.getCannonDesign().getAngleUpdateSpeed()) {
            return;
        }

        if (!cannon.hasSentryEntity() && cannon.isSentryHomedAfterFiring()) {
            return;
        }
        // autoaming or fineadjusting
        if (cannon.isValid()) {
            updateAngle(null, cannon, null, InteractAction.adjustSentry);
        }
    }

    private void sentryLoadChest(Cannon cannon) {
        // load from chest if the cannon is in automatic mode
        if (cannon.isLoaded() || cannon.isLoading() || cannon.isFiring() || System.currentTimeMillis() <= cannon.getSentryLastLoadingFailed() + 5000) {
            return;
        }

        MessageEnum messageEnum = cannon.reloadFromChests(null, !cannon.getCannonDesign().isAmmoInfiniteForRedstone());
        if (messageEnum.isError()) {
            cannon.setSentryLastLoadingFailed(System.currentTimeMillis());
            SoundUtils.playErrorSound(cannon.getMuzzle());
            plugin.logDebug("Sentry " + cannon.getCannonName() + " loading message: " + messageEnum);
        }
    }

    private void sentryFiring(Cannon cannon) {
        if (!cannon.hasSentryEntity() || !cannon.targetInSight()) {
            return;
        }

        if (!cannon.isReadyToFire() || System.currentTimeMillis() <= cannon.getSentryLastFiringFailed() + 2000) {
            return;
        }

        MessageEnum messageEnum = plugin.getFireCannon().sentryFiring(cannon);
        if (messageEnum == null) {
            return;
        }

        plugin.logDebug("Sentry " + cannon.getCannonName() + " firing message: " + messageEnum);
        if (messageEnum.isError()) {
            cannon.setSentryLastFiringFailed(System.currentTimeMillis());
            SoundUtils.playErrorSound(cannon.getMuzzle());
        }
    }

    private void calculateFiringSolution(Cannon cannon) {
        // calculate a firing solution
        if (!cannon.isChunkLoaded() || System.currentTimeMillis() <= (cannon.getLastSentryUpdate() + cannon.getCannonDesign().getSentryUpdateTime())) {
            return;
        }
        cannon.setLastSentryUpdate(System.currentTimeMillis());

        HashMap<UUID, Target> targets = CannonsUtil.getNearbyTargets(cannon.getMuzzle(), cannon.getCannonDesign().getSentryMinRange(), cannon.getCannonDesign().getSentryMaxRange());
        //old target - is this still valid?
        handleOldTarget(cannon, targets);

        // find a suitable target
        findSuitableTarget(cannon, targets);

        //find target solution
        processExistingSentryEntity(cannon, targets);
    }

    private void processExistingSentryEntity(Cannon cannon, HashMap<UUID, Target> targets) {
        if (!cannon.hasSentryEntity()) {
            return;
        }
        Target target = targets.get(cannon.getSentryEntity());
        // find exact solution for the cannon
        if (!calculateTargetSolution(cannon, target, true)) {
            //no exact solution found for this target. So skip it and try it again in the next run
            cannon.setSentryEntity(null);
            return;
            //cannon.setLastSentryUpdate(System.currentTimeMillis() - cannon.getCannonDesign().getSentryUpdateTime());
        }

        CannonTargetEvent targetEvent = new CannonTargetEvent(cannon, target);
        Bukkit.getServer().getPluginManager().callEvent(targetEvent);
        if (targetEvent.isCancelled()) {
            //event cancelled
            plugin.logDebug("can't find solution for target");
            cannon.setSentryEntity(null);
        } else {
            cannon.setSentryEntity(target.uniqueId());
        }
    }

    private void findSuitableTarget(Cannon cannon, Map<UUID, Target> targets) {
        if (cannon.hasSentryEntity()) {
            return;
        }

        LinkedList<Target> possibleTargets = new LinkedList<>();

        for (Target t : targets.values()) {
            TargetType type = t.targetType();
            if (!type.isAllowed(cannon)) continue;
            //Monster
            if (type == TargetType.MONSTER) {
                handlePossibleTarget(cannon, t, possibleTargets);
                continue;
            }


            if (checkScoreboard(cannon, t)) continue;
            if (cannon.isWhitelisted(t.uniqueId())) continue;
            //Player
            if (type == TargetType.PLAYER) {

                final boolean[] skip = new boolean[1];
                Cannons.getPlugin().getHookManager().processIfPresent(TownyHook.class, townyApi -> {
                    Resident ownerResident = townyApi.getResident(cannon.getOwner());
                    if (ownerResident == null) {
                        return;
                    }

                    Resident residentTarget = townyApi.getResident(t.uniqueId());
                    if (residentTarget == null) {
                        return;
                    }

                    if (CombatUtil.isAlly(residentTarget, ownerResident)) {
                        skip[0] = true;
                    }
                });

                if (skip[0]) continue;

                // get solution
                handlePossibleTarget(cannon, t, possibleTargets);
                continue;
            }

            Cannon tCannon = CannonManager.getCannon(t.uniqueId());
            if (tCannon == null) continue;
            //Cannons & Other have same handling
            //check if the owner is whitelisted
            if (type == TargetType.CANNON || type == TargetType.OTHER) {
                //check if the owner is whitelisted
                handlePossibleTarget(cannon, t, possibleTargets);
            }
        }
        //so we have some targets
        if (possibleTargets.isEmpty()) {
            return;
        }

        //select one target
        possibleTargets.stream()
                .map(Target::uniqueId)
                .filter(t -> !cannon.wasSentryTarget(t))
                .findFirst()
                .ifPresent(cannon::setSentryEntity);
        if (!cannon.hasSentryEntity()) {
            cannon.setSentryEntity(possibleTargets.get(0).uniqueId());
        }
    }

    private void handlePossibleTarget(Cannon cannon, Target t, List<Target> possibleTargets) {
        if (canFindTargetSolution(cannon, t)) {
            possibleTargets.add(t);
        }
    }

    private boolean checkScoreboard(Cannon cannon, Target t) {
        Player p = Bukkit.getPlayer(t.uniqueId());
        Scoreboard scoreboard = getScoreboard();
        // check team board
        if (p != null && scoreboard != null) {
            Team team = scoreboard.getPlayerTeam(p);
            return team != null && team.hasPlayer(Bukkit.getOfflinePlayer(cannon.getOwner()));
        }
        return false;
    }

    private void handleOldTarget(Cannon cannon, Map<UUID, Target> targets) {
        if (!cannon.hasSentryEntity()) {
            return;
        }

        Target target = targets.get(cannon.getSentryEntity());
        if (System.currentTimeMillis() > cannon.getSentryTargetingTime() + cannon.getCannonDesign().getSentrySwapTime() || !targets.containsKey(cannon.getSentryEntity())) {
            cannon.setSentryEntity(null);
        } else if (!canFindTargetSolution(cannon, target)) {
            //is the previous target still valid
            cannon.setSentryEntity(null);
        }
    }

    private boolean canFindTargetSolution(Cannon cannon, Target target) {
        return canFindTargetSolution(cannon, target, target.centerLocation());
    }

    /**
     * find a possible solution to fire the cannon - this is just an estimation
     *
     * @param cannon         the cannon which is operated
     * @param loctarget      lcoation of the target
     * @return true if the cannon can fire on this target
     */
    private boolean canFindTargetSolution(Cannon cannon, Target target, Location loctarget) {
        if (!cannon.getWorld().equals(loctarget.getWorld().getUID()))
            return false;

        //plugin.logDebug("old target " + target);
        Location newTarget = loctarget.clone();
        Location muzzle = cannon.getMuzzle();
        int maxSentryRange = cannon.getCannonDesign().getSentryMaxRange();

        if (target.centerLocation().distanceSquared(muzzle) > maxSentryRange * maxSentryRange) {
            return false;
        }

        int ignoredBlocks = 0;

        if (cannon.isProjectileLoaded()) {
            Projectile proj = cannon.getLoadedProjectile();
            if (proj != null)
                ignoredBlocks = proj.getSentryIgnoredBlocks();
        }

        if (target.targetType() == TargetType.CANNON)
            ignoredBlocks = 1;
        if (!CannonsUtil.hasLineOfSight(muzzle, loctarget, ignoredBlocks)) {
            return false;
        }

        Vector direction = newTarget.toVector().subtract(muzzle.toVector());
        double yaw = CannonsUtil.vectorToYaw(direction);
        double pitch = CannonsUtil.vectorToPitch(direction);
        //can the cannon fire on this player
        if (cannon.canAimYaw(yaw)) {
            cannon.setAimingYaw(yaw);
            cannon.setAimingPitch(pitch);
            return true;
        }
        return false;
    }

    /**
     * find exact solution to fire the cannon
     *
     * @param cannon         the cannon which is operated
     * @param target         lcoation of the target
     * @return true if a solution was found
     */
    private boolean calculateTargetSolution(Cannon cannon, Target target, boolean addSpread) {
        Location targetLoc = target.centerLocation();
        //aim for the center of the target if there is an area effect of the projectile
        if (cannon.getLoadedProjectile() != null && (cannon.getLoadedProjectile().getExplosionPower() > 2. || (cannon.getLoadedProjectile().getPlayerDamage() > 1. && cannon.getLoadedProjectile().getPlayerDamageRange() > 2.)))
            targetLoc = target.groundLocation();

        if (!canFindTargetSolution(cannon, target))
            return false;

        if (cannon.getCannonballVelocity() < 0.01)
            return false;

        //plugin.logDebug("calculate Target solution for target at: " + targetLoc.toVector());

        //starting values
        boolean found = false;
        double step = 10.;
        int maxInterations = 100;
        double sign = 1.;
        if (cannon.getCannonDesign().isSentryIndirectFire()) {
            sign = -1.;
            cannon.setAimingPitch(cannon.getMaxVerticalPitch());
            maxInterations = 500;
        }
        // do the first step, because the starting solution is always true
        cannon.setAimingPitch(cannon.getAimingPitch() - sign * step);

        for (int i = 0; i < 100; i++) {
            Vector fvector = CannonsUtil.directionToVector(cannon.getAimingYaw(), cannon.getAimingPitch(), cannon.getCannonballVelocity());
            double diffY = simulateShot(fvector, cannon.getMuzzle(), targetLoc, cannon.getProjectileEntityType(), maxInterations);

            if (!cannon.getCannonDesign().isSentryIndirectFire() && Math.abs(diffY) > 1000.0) {
                // plugin.logDebug("diffY too large: " + diffY);
                return false;
            }
            //direct fire
            if (diffY < 0) {
                //below target
                if (found)
                    step /= 2.;
                cannon.setAimingPitch(cannon.getAimingPitch() - sign * step);
            } else {
                //aiming above target - valid solution
                found = true;
                step /= 2.;
                cannon.setAimingPitch(cannon.getAimingPitch() + sign * step);
            }

            if (step >= cannon.getCannonDesign().getAngleStepSize()) {
                continue;
            }

            if (!verifyTargetSolution(cannon, target, 2.)) {
                return false;
            }

            //can the cannon aim at this solution
            if (addSpread) {
                cannon.setAimingPitch(cannon.getAimingPitch() + cannon.getCannonDesign().getSentrySpread() * random.nextGaussian());
                cannon.setAimingYaw(cannon.getAimingYaw() + cannon.getCannonDesign().getSentrySpread() * random.nextGaussian());
            }

            return cannon.canAimPitch(cannon.getAimingPitch()) && cannon.canAimYaw(cannon.getAimingYaw());
            // can't aim at this solution
        }
        return false;
    }

    /**
     * verifies if the trajectory is blocked by terrain
     *
     * @param cannon      the firing cannon
     * @param target      the target to fire at
     * @param maxdistance allowed distance of the target to the impact location
     * @return true if the target is not blocked or close to the impact
     */
    private boolean verifyTargetSolution(Cannon cannon, Target target, double maxdistance) {
        Location muzzle = cannon.getMuzzle();
        Vector vel = cannon.getTargetVector();

        MovingObject predictor = new MovingObject(muzzle, vel, cannon.getProjectileEntityType());
        Vector start = muzzle.toVector();

        int maxInterations = 500;
        double targetDist = 100000000000000.;

        //make a few iterations until we hit something
        for (int i = 0; start.distance(predictor.getLoc()) < cannon.getCannonDesign().getSentryMaxRange() * 1.2 && i < maxInterations; i++) {
            //is target distance shorter than before
            Location predictorLoc = predictor.getLocation();
            double newDist = predictorLoc.distance(target.centerLocation());

            if (!(newDist < targetDist)) {
                // missed the target
                return true;
            }
            targetDist = newDist;
            //see if we hit something, but wait until the cannonball is 1 block away (safety first)
            Block block = predictorLoc.getBlock();
            if (start.distance(predictor.getLoc()) > 1. && !block.isEmpty()) {
                predictor.revertProjectileLocation(false);
                return CannonsUtil.findSurface(predictorLoc, predictor.getVel()).distance(target.centerLocation()) < maxdistance;
            }
            predictor.updateProjectileLocation(false);
        }
        return false;
    }

    /**
     * calculates the height of the projectile at the target distance
     *
     * @param vector firing vector
     * @param muzzle start point of the cannonball
     * @param target target for the cannonball
     * @return distance how much above/below the projectile will hit
     */
    private double simulateShot(Vector vector, Location muzzle, Location target, EntityType projectileType, int maxInterations) {
        MovingObject cannonball = new MovingObject(muzzle, vector, projectileType);
        double target_distance_squared = Math.pow(target.getX() - muzzle.getX(), 2) + Math.pow(target.getZ() - muzzle.getZ(), 2);

        Vector oldLoc = null;
        for (int i = 0; i < 500; i++) {
            cannonball.updateProjectileLocation(false);
            Vector cLoc = cannonball.getLoc();
            double calculated_distance_squared = Math.pow(cLoc.getX() - muzzle.getX(), 2) + Math.pow(cLoc.getZ() - muzzle.getZ(), 2);

            if (calculated_distance_squared > target_distance_squared) {
                //calculate intersection
                if (oldLoc == null)
                    return cLoc.getY() - target.getY();
                Vector vel = cannonball.getVel().clone();
                double dist1 = Math.sqrt(vel.getX() * vel.getX() + vel.getY() * vel.getY() + vel.getZ() * vel.getZ());
                double dist2 = oldLoc.distance(target.toVector());
                Vector inter = oldLoc.add(vel.multiply(dist2 / dist1));
                return inter.getY() - target.getY();
            }
            oldLoc = cLoc.clone();
        }
        //could not find an intersection
        return -1000000000000.0;
    }

    /**
     * remove entity as sentry target (e.g. in case of death)
     *
     * @param entity entity to remove
     */
    public void removeTarget(Entity entity) {
        if (entity == null)
            return;

        for (UUID sentryCannon : sentryCannons) {
            Cannon cannon = CannonManager.getCannon(sentryCannon);
            if (cannon.getSentryEntity() != null && cannon.getSentryEntity().equals(entity.getUniqueId())) {
                cannon.setSentryEntity(null);
            }
        }
    }

    /**
     * switches aming mode for this cannon
     *
     * @param player - player in aiming mode
     * @param cannon - operated cannon
     */
    public void aimingMode(Player player, Cannon cannon, boolean fire) {
        if (player == null)
            return;

        boolean isAimingMode = inAimingMode.containsKey(player.getUniqueId());
        if (isAimingMode) {
            if (cannon == null)
                cannon = getCannonInAimingMode(player);

            //this player is already in aiming mode, he might fire the cannon or turn the aiming mode off
            MessageEnum message = fire ?
                    plugin.getFireCannon().playerFiring(cannon, player, InteractAction.fireAutoaim) :
                    disableAimingMode(player);
            //turn off the aiming mode
            userMessages.sendMessage(message, player, cannon);
            return;
        }

        //enable aiming mode. Sentry cannons can't be operated by players
        if (cannon == null || cannon.getCannonDesign().isSentry() && cannon.isSentryAutomatic()) {
            return;
        }

        //check if player has permission to aim
        if (!player.hasPermission(cannon.getCannonDesign().getPermissionAutoaim())) {
            //no Permission to aim
            userMessages.sendMessage(MessageEnum.PermissionErrorAdjust, player, cannon);
            return;
        }

        //check distance before enabling the cannon
        if (!distanceCheck(player, cannon)) {
            userMessages.sendMessage(MessageEnum.AimingModeTooFarAway, player, cannon);
            return;
        }

        MessageEnum message = enableAimingMode(player, cannon);
        if (message == MessageEnum.AimingModeEnabled)
            SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundEnableAimingMode());
        else
            SoundUtils.playErrorSound(cannon.getMuzzle());
        userMessages.sendMessage(message, player, cannon);
    }

    /**
     * enable the aiming mode
     *
     * @param player player who operates the cannon
     * @param cannon the cannon in aiming mode
     * @return message for the user
     */
    public MessageEnum enableAimingMode(Player player, Cannon cannon) {
        if (player == null)
            return null;

        //sentry can't be in aiming mode if active
        if (cannon == null || (cannon.getCannonDesign().isSentry() && cannon.isSentryAutomatic()))
            return null;

        if (!player.hasPermission(cannon.getCannonDesign().getPermissionAutoaim()))
            return MessageEnum.PermissionErrorAutoaim;

        inAimingMode.put(player.getUniqueId(), cannon.getUID());

        MessageEnum message = cannon.addCannonOperator(player, true);
        if (message != MessageEnum.AimingModeEnabled)
            return message;

        //todo add player from all cannons as cannon operator
        if (!cannon.getCannonDesign().isLinkCannonsEnabled()) {
            return MessageEnum.AimingModeEnabled;
        }

        int d = cannon.getCannonDesign().getLinkCannonsDistance() * 2;
        LinkedList<Cannon> cannonList = new LinkedList<>(CannonManager.getCannonsInBox(cannon.getLocation(), d, d, d));

        CannonLinkAimingEvent event = new CannonLinkAimingEvent(cannon, player, cannonList, false);
        Bukkit.getServer().getPluginManager().callEvent(event);

        for (Cannon fcannon : event.getCannonList()) {
            if (fcannon.getCannonDesign().equals(cannon.getCannonDesign()) || event.isSameDesign())
                fcannon.addCannonOperator(player, false);
        }

        return MessageEnum.AimingModeEnabled;
    }


    /**
     * disables the aiming mode for this player
     *
     * @param player - player in aiming mode
     * @return message for the player
     */
    public MessageEnum disableAimingMode(Player player) {
        //player.playSound(player.getEyeLocation(), Sound.MINECART_BASE, 0.25f, 0.75f);
        Cannon cannon = getCannonInAimingMode(player);
        if (cannon != null)
            SoundUtils.playSound(player.getEyeLocation(), cannon.getCannonDesign().getSoundDisableAimingMode());
        return disableAimingMode(player, cannon);
    }

    /**
     * disables the aiming mode for this player
     *
     * @param player player in aiming mode
     * @param cannon operated cannon
     * @return message for the player
     */
    public MessageEnum disableAimingMode(Player player, Cannon cannon) {
        if (player == null)
            return null;

        if (!inAimingMode.containsKey(player.getUniqueId())) {
            return null;
        }
        //player in map -> remove
        inAimingMode.remove(player.getUniqueId());

        if (cannon == null) {
            return MessageEnum.AimingModeDisabled;
        }
        cannon.removeCannonOperator();

        // todo remove player from all cannons as cannon operator
        if (!cannon.getCannonDesign().isLinkCannonsEnabled()) {
            return MessageEnum.AimingModeDisabled;
        }

        int d = cannon.getCannonDesign().getLinkCannonsDistance() * 2;
        for (Cannon fcannon : CannonManager.getCannonsInBox(cannon.getLocation(), d, d, d)) {
            if (fcannon.getCannonDesign().equals(cannon.getCannonDesign()))
                cannon.removeCannonOperator();
        }

        return MessageEnum.AimingModeDisabled;
    }

    /**
     * set a new aiming target for the given cannon by direction
     *
     * @param cannon operated cannon
     * @param loc    new yaw and pitch angles
     */
    public void setAimingTarget(Cannon cannon, Location loc) {
        if (cannon == null)
            return;

        cannon.setAimingYaw(loc.getPitch());
        cannon.setAimingPitch(loc.getYaw());
    }

    /**
     * set a new aiming target for the given cannon
     *
     * @param cannon operated cannon
     * @param yaw    new yaw
     * @param pitch  new pitch
     */
    public void setAimingTarget(Cannon cannon, double yaw, double pitch) {
        if (cannon == null)
            return;

        cannon.setAimingYaw(yaw);
        cannon.setAimingPitch(pitch);
    }

    /**
     * finds the right message for the horizontal angle change
     *
     * @param cannon operated cannon
     * @return message from the cannon
     */
    private MessageEnum setMessageHorizontal(Cannon cannon, boolean combinedAngle) {
        if (combinedAngle)
            return MessageEnum.SettingCombinedAngle;
        //correct some angle messages
        if (cannon.getHorizontalAngle() > 0) {
            //aiming to the right
            return MessageEnum.SettingHorizontalAngleRight;
        } else {
            //aiming to the left
            return MessageEnum.SettingHorizontalAngleLeft;
        }
    }

    /**
     * finds the right message for the vertical angle change
     *
     * @param cannon operated cannon
     * @return message for the player
     */
    private MessageEnum setMessageVertical(Cannon cannon, boolean combinedAngle) {
        if (combinedAngle)
            return MessageEnum.SettingCombinedAngle;
        if (cannon.getVerticalAngle() > 0) {
            //aiming to the down
            return MessageEnum.SettingVerticalAngleUp;
        } else {
            //aiming to the up
            return MessageEnum.SettingVerticalAngleDown;
        }
    }

    /**
     * show a line where the cannon is aiming
     *
     * @param cannon - operated cannon
     * @param player - player operating the cannon
     */
    public void showAimingVector(Cannon cannon, Player player) {
        if (player == null || cannon == null)
            return;

        // Imitation of angle
        if (config.isImitatedAimingEnabled() && isImitatingEnabled(player.getUniqueId())) {
            FakeBlockHandler.getInstance().imitateLine(player, cannon.getMuzzle(), cannon.getAimingVector(), 0,
                    config.getImitatedAimingLineLength(), config.getImitatedAimingMaterial(), FakeBlockType.AIMING, config.getImitatedAimingTime());
        }
    }

    public void toggleImitating(Player player) {
        if (!isImitatingEnabled(player.getUniqueId())) {
            enableImitating(player);
        } else {
            disableImitating(player);
        }
    }

    public void disableImitating(Player player) {
        userMessages.sendMessage(MessageEnum.ImitatedEffectsDisabled, player);
        //it is enabled on default, adding to this list will stop the aiming effect
        imitatedEffectsOff.add(player.getUniqueId());
    }

    public boolean isImitatingEnabled(UUID playerUID) {
        //it is enabled on default, adding to this list will stop the aiming effect
        return !imitatedEffectsOff.contains(playerUID);
    }

    public void enableImitating(Player player) {
        userMessages.sendMessage(MessageEnum.ImitatedEffectsEnabled, player);
        //it is enabled on default, adding to this list will stop the aiming effect
        imitatedEffectsOff.remove(player.getUniqueId());
    }

    /**
     * updates the last time usage of the cannon
     *
     * @param cannon operated cannon
     */
    public void updateLastAimed(Cannon cannon) {
        lastAimed.put(cannon.getUID(), System.currentTimeMillis());
    }

    /**
     * removes this cannon from the list of last time usage
     *
     * @param cannon operated cannon
     */
    public void removeCannon(Cannon cannon) {
        lastAimed.remove(cannon.getUID());
    }

    /**
     * removes all entries of this player in this class
     *
     * @param player player to remove
     */
    public void removePlayer(Player player) {
        disableAimingMode(player);
    }

    /**
     * removes the observer entry for this player in all cannons
     *
     * @param player this player will be removed from the lists
     */
    public void removeObserverForAllCannons(Player player) {
        for (Cannon cannon : CannonManager.getCannonList().values()) {
            cannon.removeObserver(player);
            userMessages.sendMessage(MessageEnum.CannonObserverRemoved, player, cannon);
        }

    }

    /**
     * calculated the impact of the projectile
     *
     * @param cannon the cannon must be loaded with a projectile
     * @return the expected impact location
     */
    public Location impactPredictor(Cannon cannon) {
        if (cannon == null || cannon.getCannonballVelocity() < 0.01 || !config.isImitatedPredictorEnabled() || !cannon.getCannonDesign().isPredictorEnabled())
            return null;

        Location muzzle = cannon.getMuzzle();
        Vector vel = cannon.getFiringVector(false, false);

        MovingObject predictor = new MovingObject(muzzle, vel, cannon.getProjectileEntityType());
        Vector start = muzzle.toVector();


        //make a few iterations until we hit something
        for (int i = 0; start.distance(predictor.getLoc()) < config.getImitatedPredictorDistance() && i < config.getImitatedPredictorIterations(); i++) {
            //see if we hit something
            Location loc = predictor.getLocation();

            Block block = loc.getBlock();
            if (!block.isEmpty()) {
                predictor.revertProjectileLocation(false);
                return CannonsUtil.findSurface(loc, predictor.getVel());
            }

            predictor.updateProjectileLocation(false);
        }

        //nothing found
        //plugin.logDebug("impact predictor could not find the impact");
        return null;
    }

    /**
     * impact effects will be only be shown if the cannon is not adjusted (aiming) for a while
     */
    public void updateImpactPredictor() {
        Iterator<Map.Entry<UUID, Long>> iter = lastAimed.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<UUID, Long> last = iter.next();
            Cannon cannon = CannonManager.getCannon(last.getKey());
            if (cannon == null) {
                iter.remove();
                continue;
            }

            CannonDesign design = cannon.getCannonDesign();
            if (last.getValue() + design.getPredictorDelay() >= System.currentTimeMillis()) {
                continue;
            }
            //reset the aiming so we have the do the next update after the update time
            last.setValue(System.currentTimeMillis() - design.getPredictorDelay() + design.getPredictorUpdate());

            //find all the watching players
            HashMap<UUID, Boolean> nameList = cannon.getObserverMap();
            if (nameList.isEmpty()) {
                //remove wrong entries and cannon with no observer (we don't need to update them)
                iter.remove();
                continue;
            }

            Location impact = impactPredictor(cannon);
            Iterator<Map.Entry<UUID, Boolean>> entry = nameList.entrySet().iterator();
            while (entry.hasNext()) {
                Map.Entry<UUID, Boolean> nextName = entry.next();
                Player player = Bukkit.getPlayer(nextName.getKey());
                //show impact to the player
                if (player != null && impact != null && FakeBlockHandler.getInstance().belowMaxLimit(player, impact)) {
                    FakeBlockHandler.getInstance().imitatedSphere(player, impact, 1, config.getImitatedPredictorMaterial(), FakeBlockType.IMPACT_PREDICTOR, config.getImitatedPredictorTime());
                }
                //remove entry if there removeEntry enabled, or player is offline
                if (nextName.getValue() || player == null) {
                    plugin.logDebug("remove " + nextName.getKey() + " from observerlist");
                    entry.remove();
                }
            }
        }
    }

    /**
     * calculated the impact of the projectile and make a sphere with fakeBlocks at the impact for the given player
     *
     * @param cannon the cannon must be loaded with a projectile
     * @param player only this player will see this impact marker blocks
     * @return the expected impact location
     */
    public Location impactPredictor(Cannon cannon, Player player) {
        Location surface = impactPredictor(cannon);
        FakeBlockHandler.getInstance().imitatedSphere(player, surface, 1, config.getImitatedPredictorMaterial(), FakeBlockType.IMPACT_PREDICTOR, config.getImitatedPredictorTime());
        return surface;
    }

    /**
     * adds the cannon to the list of sentry guns. This cannons will be operate on its own.
     *
     * @param cannonId cannon to add
     */
    public void addSentryCannon(UUID cannonId) {
        if (cannonId != null) {
            sentryCannons.add(cannonId);
        }
    }

    /**
     * removes the cannon from the list of sentry guns. This cannons will be operate on its own.
     *
     * @param cannonId cannon to add
     */
    public void removeSentryCannon(UUID cannonId) {
        if (cannonId != null) {
            sentryCannons.remove(cannonId);
        }
    }

    /**
     * calculates the new solution for the sentry cannons
     *
     * @param cannon sentry cannon
     * @param target target of the cannon
     * @return angles for the cannon
     */
    private GunAngles calctSentrySolution(Cannon cannon, Location target) {
        return new GunAngles(0., 0.);
    }

    /**
     * returns true if the player is currently in aiming mode
     *
     * @param player the player in aiming mode
     * @return true if the player is in aiming mode
     */
    public boolean isInAimingMode(UUID player) {
        return player != null && inAimingMode.containsKey(player);
    }
}
