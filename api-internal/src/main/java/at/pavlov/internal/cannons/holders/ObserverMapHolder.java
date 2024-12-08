package at.pavlov.internal.cannons.holders;

import at.pavlov.internal.enums.MessageEnum;
import org.jetbrains.annotations.NotNull;

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
    default MessageEnum removeObserver(@NotNull UUID player) {
        getObserverMap().remove(player);
        return MessageEnum.CannonObserverRemoved;
    }

    /**
     * is the given Player listed as observer for this cannons
     *
     * @param player player to test
     * @return true is player is listed as observer
     */
    default boolean isObserver(@NotNull UUID player) {
        return this.getObserverMap().containsKey(player);
    }

    /**
     * add the player as observer for this cannon
     *
     * @param player             player will be added as observer
     * @param removeAfterShowing if true, the observer only works once
     * @return message for the player
     */
    MessageEnum addObserver(@NotNull UUID player, boolean removeAfterShowing);

    /**
     * toogles the player as observer for this cannon
     *
     * @param player             player will be added as observer
     * @param removeAfterShowing if true, the observer only works once
     * @return message for the player
     */
    default MessageEnum toggleObserver(@NotNull UUID player, boolean removeAfterShowing) {
        if (getObserverMap().containsKey(player))
            return removeObserver(player);
        else
            return addObserver(player, removeAfterShowing);
    }
}
