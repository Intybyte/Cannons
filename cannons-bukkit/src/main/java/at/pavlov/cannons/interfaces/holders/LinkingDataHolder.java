package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.Enum.MessageEnum;
import at.pavlov.internal.cannon.data.LinkingData;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface LinkingDataHolder {
    LinkingData getLinkingData();
    void setLinkingData(LinkingData data);

    default UUID getCannonOperator() {
        return this.getLinkingData().getCannonOperator();
    }

    default void setCannonOperator(UUID cannonOperator) {
        this.getLinkingData().setCannonOperator(cannonOperator);
    }

    default boolean hasLinkedOperator() {
        return this.getCannonOperator() != null;
    }

    default boolean isMasterCannon() {
        return getLinkingData().isMasterCannon();
    }

    default void setMasterCannon(boolean masterCannon) {
        this.getLinkingData().setMasterCannon(masterCannon);
    }


    /**
     * checks it this cannon has a cannon operator (linked or master)
     *
     * @return true if the cannon has a cannon operator
     */
    default boolean hasCannonOperator() {
        return this.getCannonOperator() != null;
    }

    /**
     * add the player as cannon operator for this cannon as master cannon
     *
     * @param player player will be added as cannon operator
     * @return message for the player
     */
    default MessageEnum addCannonOperator(Player player) {
        return addCannonOperator(player, true);
    }

    /**
     * add the player as cannon operator for this cannon, if
     *
     * @param player       player will be added as cannon operator
     * @param masterCannon if the controlled cannon is a slave and not the master cannon
     * @return message for the player
     */
    MessageEnum addCannonOperator(Player player, Boolean masterCannon);

    /**
     * removes the player as observer
     *
     * @return message for the player
     */
    default MessageEnum removeCannonOperator() {
        setCannonOperator(null);
        setMasterCannon(true);
        return MessageEnum.AimingModeDisabled;
    }

    /**
     * is the given Player listed as observer for this cannons
     *
     * @param player player to test
     * @return true is player is listed as cannon operator
     */
    default boolean isCannonOperator(Player player) {
        UUID cannonOperator = getCannonOperator();
        if (cannonOperator == null)
            return false;

        return cannonOperator.equals(player.getUniqueId());
    }
}
