package at.pavlov.cannons.hooks.towny;

import at.pavlov.cannons.cannon.CannonManager;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.actions.TownyActionEvent;
import com.palmergames.bukkit.towny.object.TownBlock;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownyListeners implements Listener {

    private static final CannonManager manager = CannonManager.getInstance();

    @EventHandler
    public void onAction(TownyActionEvent event) {
        TownyAPI api = TownyAPI.getInstance();
        Location location = event.getLocation();
        TownBlock townBlock = api.getTownBlock(location);
        if (townBlock == null) return;

        if (!townBlock.getType().equals(TownyHook.getArtilleryType())) return;

        boolean isCannon = manager.getCannon(location, event.getPlayer().getUniqueId()) != null;
        if (isCannon) {
            event.setCancelled(false);
        }
    }
}
