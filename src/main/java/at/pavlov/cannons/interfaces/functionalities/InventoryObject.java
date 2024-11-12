package at.pavlov.cannons.interfaces.functionalities;

import at.pavlov.cannons.Enum.MessageEnum;
import org.bukkit.inventory.Inventory;

import java.util.List;
import java.util.UUID;

public interface InventoryObject {
    MessageEnum reloadFromChests(UUID player, boolean consumesAmmo);
    List<Inventory> getInventoryList();
}
