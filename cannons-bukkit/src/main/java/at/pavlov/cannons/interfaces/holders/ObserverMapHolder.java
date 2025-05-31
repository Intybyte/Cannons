package at.pavlov.cannons.interfaces.holders;

import at.pavlov.internal.enums.MessageEnum;
import org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public interface ObserverMapHolder {
    HashMap<UUID, Boolean> getObserverMap();
    //no set as it shouldn't be changed directly

    /**
     * removes the player as observer
     *
     * @param player player will be removed as observer
     * @return message for the player
     */
    default MessageEnum removeObserver(Player player) {
        Validate.notNull(player, "player must not be null");

        getObserverMap().remove(player.getUniqueId());
        return MessageEnum.CannonObserverRemoved;
    }

    /**
     * is the given Player listed as observer for this cannons
     *
     * @param player player to test
     * @return true is player is listed as observer
     */
    default boolean isObserver(Player player) {
        return this.getObserverMap().containsKey(player.getUniqueId());
    }

    /**
     * add the player as observer for this cannon
     *
     * @param player             player will be added as observer
     * @param removeAfterShowing if true, the observer only works once
     * @return message for the player
     */
    MessageEnum addObserver(Player player, boolean removeAfterShowing);

    /**
     * toogles the player as observer for this cannon
     *
     * @param player             player will be added as observer
     * @param removeAfterShowing if true, the observer only works once
     * @return message for the player
     */
    default MessageEnum toggleObserver(Player player, boolean removeAfterShowing) {
        Validate.notNull(player, "player must not be null");

        if (getObserverMap().containsKey(player.getUniqueId()))
            return removeObserver(player);
        else
            return addObserver(player, removeAfterShowing);
    }
}
