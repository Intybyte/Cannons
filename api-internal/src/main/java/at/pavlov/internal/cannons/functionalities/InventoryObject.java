package at.pavlov.internal.cannons.functionalities;

import at.pavlov.internal.enums.MessageEnum;

import java.util.List;
import java.util.UUID;

public interface InventoryObject<Inventory> {
    MessageEnum reloadFromChests(UUID player, boolean consumesAmmo);
    List<Inventory> getInventoryList();
}
