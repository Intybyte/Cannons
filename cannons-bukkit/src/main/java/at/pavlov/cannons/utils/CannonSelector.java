package at.pavlov.cannons.utils;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.cannons.Enum.SelectCannon;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.config.UserMessages;
import at.pavlov.cannons.exchange.EmptyExchanger;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CannonSelector {
    @Getter
    private static CannonSelector instance = null;

    private final HashMap<UUID, SelectCannon> cannonSelector = new HashMap<>();
    //<player,command to be performed>;
    private final HashMap<UUID, Boolean> selectTargetBoolean = new HashMap<>();
    //<player,playerUID>;
    private final HashMap<UUID, UUID> whitelistPlayer = new HashMap<>();

    private final UserMessages userMessages;
    private final Cannons plugin;

    private CannonSelector(Cannons plugin) {
        this.userMessages = UserMessages.getInstance();
        this.plugin = plugin;
    }

    public static void initialize(Cannons plugin) {
        instance = new CannonSelector(plugin);
    }

    /**
     * this player will be removed from the selecting mode
     *
     * @param player the player will be removed
     * @param cmd    this command will be performed when the cannon is selected
     */
    public void addCannonSelector(Player player, SelectCannon cmd) {
        if (player == null || cmd == null)
            return;

        if (isSelectingMode(player)) {
            return;
        }

        cannonSelector.put(player.getUniqueId(), cmd);

        if (isBlockSelectingMode(player)) {
            userMessages.sendMessage(MessageEnum.CmdSelectBlock, player);
        } else {
            userMessages.sendMessage(MessageEnum.CmdSelectCannon, player);
        }
    }

    /**
     * this player will be removed from the selecting mode
     *
     * @param player the player will be removed
     */
    public void removeCannonSelector(Player player) {
        if (player == null)
            return;

        if (isSelectingMode(player)) {
            cannonSelector.remove(player.getUniqueId());
            userMessages.sendMessage(MessageEnum.CmdSelectCanceled, player);
        }
    }

    public void toggleCannonSelector(Player player, SelectCannon cmd, OfflinePlayer offPlayer) {
        if (cmd != SelectCannon.WHITELIST_REMOVE && cmd != SelectCannon.WHITELIST_ADD) {
            throw new RuntimeException("toggleCannonSelector(Player, SelectCannon, OfflinePlayer) called in bad context");
        }
        whitelistPlayer.put(player.getUniqueId(), offPlayer.getUniqueId());
        toggleCannonSelector(player, cmd);
    }

    /**
     * selecting mode will be toggled
     *
     * @param player the player using the selecting mode
     * @param cmd    this command will be performed when the cannon is selected
     */
    public void toggleCannonSelector(Player player, SelectCannon cmd) {
        if (player == null)
            return;

        if (isSelectingMode(player)) {
            removeCannonSelector(player);
        } else {
            addCannonSelector(player, cmd);
        }
    }

    /**
     * this player will be removed from the buying mode
     *
     * @param player the player will be removed
     * @param cmd    this command will be performed when the cannon is selected
     */
    public void addBuyCannon(Player player, SelectCannon cmd) {
        if (player == null || cmd == null)
            return;

        if (!isSelectingMode(player)) {
            cannonSelector.put(player.getUniqueId(), cmd);
            userMessages.sendMessage(MessageEnum.CmdBuyCannon, player);
        }
    }

    /**
     * this player will be removed from the buying mode
     *
     * @param player the player will be removed
     */
    public void removeBuyCannon(Player player) {
        if (player == null)
            return;

        if (isSelectingMode(player)) {
            cannonSelector.remove(player.getUniqueId());
            userMessages.sendMessage(MessageEnum.CmdSelectCanceled, player);
        }
    }

    /**
     * buying mode will be toggled
     *
     * @param player the player using the selecting mode
     * @param cmd    this command will be performed when the cannon is selected
     */
    public void toggleBuyCannon(Player player, SelectCannon cmd) {
        if (player == null)
            return;

        if (isSelectingMode(player)) {
            removeBuyCannon(player);
        } else {
            addBuyCannon(player, cmd);
        }
    }


    /**
     * Checks if this player is in selecting mode
     *
     * @param player player to check
     * @return true if in selecting mode
     */
    public boolean isSelectingMode(Player player) {
        return player != null && cannonSelector.containsKey(player.getUniqueId());
    }

    public boolean isBlockSelectingMode(Player player) {
        SelectCannon cmd = cannonSelector.get(player.getUniqueId());
        return cmd.equals(SelectCannon.BLOCK_DATA);
    }

    /**
     * adds a new selected cannon for this player
     *
     * @param player player that selected the cannon
     * @param block  the selected block
     */
    public void setSelectedBlock(Player player, Block block) {
        if (player == null || block == null)
            return;

        SelectCannon cmd = cannonSelector.get(player.getUniqueId());
        if (cmd == SelectCannon.BLOCK_DATA) {
            player.sendMessage(block.getBlockData().getAsString());
        }
        cannonSelector.remove(player.getUniqueId());
    }

    public void setSelectedCannon(Player player, Cannon cannon) {
        var key = player.getUniqueId();
        setSelectedCannon(player, cannon, cannonSelector.get(key), selectTargetBoolean.get(key));
    }

    /**
     * adds a new selected cannon for this player
     *
     * @param player player that selected the cannon
     * @param cannon the selected cannon
     */
    public void setSelectedCannon(Player player, Cannon cannon, SelectCannon cmd, Boolean choice) {
        selectTargetBoolean.put(player.getUniqueId(), choice);
        cannonSelector.put(player.getUniqueId(), cmd);

        if (player == null || cannon == null)
            return;

        if (cmd == null) {
            selectTargetBoolean.remove(player.getUniqueId());
            cannonSelector.remove(player.getUniqueId());
            return;
        }

        UUID playerUUID = player.getUniqueId();
        switch (cmd) {
            case OBSERVER -> {
                MessageEnum message = cannon.toggleObserver(player, false);
                userMessages.sendMessage(message, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());
            }
            case INFO -> {
                userMessages.sendMessage(MessageEnum.CannonInfo, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());
            }
            case DISMANTLE -> {
                CannonManager.getInstance().dismantleCannon(cannon, player);
            }
            case WHITELIST_ADD -> {
                if (!cannon.getCannonDesign().isSentry()) {
                    userMessages.sendMessage(MessageEnum.CmdNoSentryWhitelist, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                if (!playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                } else {
                    cannon.addWhitelistPlayer(whitelistPlayer.get(playerUUID));
                    whitelistPlayer.remove(playerUUID);
                    userMessages.sendMessage(MessageEnum.CmdAddedWhitelist, player, cannon);
                    SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());
                }
            }
            case WHITELIST_REMOVE -> {
                if (!cannon.getCannonDesign().isSentry()) {
                    userMessages.sendMessage(MessageEnum.CmdNoSentryWhitelist, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                if (!playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                UUID subject = whitelistPlayer.get(playerUUID);
                if (subject.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.CmdRemovedWhitelistOwner, player, cannon);
                } else {
                    cannon.removeWhitelistPlayer(subject);
                    whitelistPlayer.remove(playerUUID);
                    userMessages.sendMessage(MessageEnum.CmdRemovedWhitelist, player, cannon);
                }
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());
            }
            case TARGET_MOB -> {
                if (!playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                if (!cannon.getCannonDesign().isSentry()) {
                    break;
                }
                // use preselected choice or toggle
                if (selectTargetBoolean.containsKey(playerUUID))
                    cannon.setTargetMob(selectTargetBoolean.get(playerUUID));
                else
                    cannon.toggleTargetMob();
                userMessages.sendMessage(MessageEnum.CmdToggledTargetMob, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());

            }
            case TARGET_PLAYER -> {
                if (!playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                if (!cannon.getCannonDesign().isSentry()) {
                    break;
                }
                // use preselected choice or toggle
                if (selectTargetBoolean.containsKey(playerUUID))
                    cannon.setTargetPlayer(selectTargetBoolean.get(playerUUID));
                else
                    cannon.toggleTargetPlayer();
                userMessages.sendMessage(MessageEnum.CmdToggledTargetPlayer, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());

            }
            case TARGET_CANNON -> {
                if (cannon.getCannonDesign().isSentry() && !playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }
                if (!cannon.getCannonDesign().isSentry()) {
                    break;
                }
                // use preselected choice or toggle
                if (selectTargetBoolean.containsKey(playerUUID))
                    cannon.setTargetCannon(selectTargetBoolean.get(playerUUID));
                else
                    cannon.toggleTargetCannon();
                userMessages.sendMessage(MessageEnum.CmdToggledTargetCannon, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());

            }
            case TARGET_OTHER -> {
                if (cannon.getCannonDesign().isSentry() && !playerUUID.equals(cannon.getOwner())) {
                    userMessages.sendMessage(MessageEnum.ErrorNotTheOwner, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                if (!cannon.getCannonDesign().isSentry()) {
                    break;
                }
                // use preselected choice or toggle
                if (selectTargetBoolean.containsKey(playerUUID))
                    cannon.setTargetOther(selectTargetBoolean.get(playerUUID));
                else
                    cannon.toggleTargetOther();
                userMessages.sendMessage(MessageEnum.CmdToggledTargetOther, player, cannon);
                SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());

            }
            case BUY_CANNON -> {
                if (cannon.isPaid()) {
                    userMessages.sendMessage(MessageEnum.ErrorAlreadyPaid, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                    break;
                }

                var result = cannon.getCannonDesign().getEconomyBuildingCost().execute(player, cannon);

                if (!result) {
                    userMessages.sendMessage(MessageEnum.ErrorNoMoney, player, cannon);
                    SoundUtils.playErrorSound(cannon.getMuzzle());
                } else {
                    cannon.boughtByPlayer(playerUUID);
                    userMessages.sendMessage(MessageEnum.CmdPaidCannon, player, cannon);
                    SoundUtils.playSound(cannon.getMuzzle(), cannon.getCannonDesign().getSoundSelected());
                }
            }
            case BLOCK_DATA -> {
            }
        }
        selectTargetBoolean.remove(playerUUID);
        cannonSelector.remove(playerUUID);
    }

    public boolean containsTarget(UUID key) {
        return selectTargetBoolean.containsKey(key);
    }

    public boolean getTarget(UUID key)  {
        return selectTargetBoolean.get(key);
    }

    public void putTarget(UUID key, boolean choice) {
        selectTargetBoolean.put(key, choice);
    }

    public void removeTarget(UUID key) {
        selectTargetBoolean.remove(key);
    }
}
