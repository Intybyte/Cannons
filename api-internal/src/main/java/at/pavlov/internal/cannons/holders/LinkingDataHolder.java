package at.pavlov.internal.cannons.holders;

import at.pavlov.internal.cannons.data.LinkingData;
import at.pavlov.internal.enums.MessageEnum;

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
    default MessageEnum addCannonOperator(UUID player) {
        return addCannonOperator(player, true);
    }

    /**
     * add the player as cannon operator for this cannon, if
     *
     * @param player       player will be added as cannon operator
     * @param masterCannon if the controlled cannon is a slave and not the master cannon
     * @return message for the player
     */
    MessageEnum addCannonOperator(UUID player, Boolean masterCannon);

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
    default boolean isCannonOperator(UUID player) {
        UUID cannonOperator = getCannonOperator();
        if (cannonOperator == null)
            return false;

        return cannonOperator.equals(player);
    }
}
