package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.hooks.movecraft.MovecraftCannonTracker;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import net.countercraft.movecraft.events.CraftSinkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class SinkListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftSink(CraftSinkEvent event) {
        Set<Cannon> cannons = MovecraftUtils.getCannons(event.getCraft());
        MovecraftCannonTracker.clearCannons(event.getCraft().getUUID());
        cannons.forEach(cannon -> CannonManager.getInstance().removeCannon(cannon.getUID(), true, true, BreakCause.Explosion));
    }
}
