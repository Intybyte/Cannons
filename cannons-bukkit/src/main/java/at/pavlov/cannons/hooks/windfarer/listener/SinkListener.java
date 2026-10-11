package at.pavlov.cannons.hooks.windfarer.listener;

import at.pavlov.cannons.hooks.windfarer.datatag.CraftCannonsData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.NullCraft;
import net.countercraft.movecraft.craft.SinkingCraft;
import net.countercraft.movecraft.events.CraftSinkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class SinkListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCraftSink(CraftSinkEvent event) {
        final Craft craft = event.getCraft();
        if (craft instanceof SinkingCraft || craft instanceof NullCraft) {
            return;
        }
        final CraftCannonsData cannonsData = CraftCannonsData.of(craft);
        cannonsData.onSunk();
    }
}
