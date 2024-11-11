package at.pavlov.cannons.interfaces;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.interfaces.functionalities.CannonDataHolder;
import org.bukkit.entity.Player;

public interface ICannon extends CannonDataHolder {

    boolean sameType(ICannon cannon);

    default boolean isAccessLinkingAllowed(Cannon fcannon, Player player) {
        return !this.getCannonDesign().isAccessForOwnerOnly() || fcannon.getOwner() == player.getUniqueId();
    }
}
