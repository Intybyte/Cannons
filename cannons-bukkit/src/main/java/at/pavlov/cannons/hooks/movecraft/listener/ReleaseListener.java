package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ReleaseListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRelease(CraftReleaseEvent event) {
        MovecraftUtils.getCannons(event.getCraft()).forEach(it -> it.setOnShip(false));
    }
}
