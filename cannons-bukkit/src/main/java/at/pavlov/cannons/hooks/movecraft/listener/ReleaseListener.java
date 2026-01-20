package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.hooks.movecraft.MovecraftCannonTracker;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ReleaseListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onRelease(CraftReleaseEvent event) {
        UUID id = event.getCraft().getUUID();
        MovecraftCannonTracker.getCannons(id).forEach(it -> it.setOnShip(false));
        MovecraftCannonTracker.clearCannons(id);
    }
}
