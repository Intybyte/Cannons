package at.pavlov.bukkit.cannons;

import at.pavlov.internal.cannons.functionalities.InventoryObject;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface CannonBukkit extends CannonDataHolderBukkit, InventoryObject<Inventory> {
    boolean sameType(CannonBukkit cannonBukkit);

    default boolean isAccessLinkingAllowed(CannonBukkit fcannon, Player player) {
        return !this.getCannonDesign().isAccessForOwnerOnly() || fcannon.getOwner() == player.getUniqueId();
    }
}
