package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.hooks.movecraft.MovecraftCannonTracker;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CraftReleaseListener implements Listener {

    @EventHandler
    public void onCraftRelease(CraftReleaseEvent event) {
        MovecraftCannonTracker.clearCannons(event.getCraft().getUUID());
    }
}
