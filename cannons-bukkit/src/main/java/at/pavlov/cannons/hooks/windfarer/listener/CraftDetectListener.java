package at.pavlov.cannons.hooks.windfarer.listener;

import at.pavlov.cannons.hooks.windfarer.datatag.CraftCannonsData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.NullCraft;
import net.countercraft.movecraft.craft.SinkingCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CraftDetectListener implements Listener {

    // Fires after all detection steps ran and provided their effets, effects have not been run yet
    // But here, we need to validate our cannons!
    // Needs to be called last due to SetOnShip
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCraftDetect(final CraftDetectEvent event) {
        // Invalid crafts
        final Craft craft = event.getCraft();
        if (craft instanceof SinkingCraft || craft instanceof NullCraft) {
            return;
        }
        final CraftCannonsData cannonsData = CraftCannonsData.of(craft);
        if (!cannonsData.validateCannons(event::setFailMessage, craft)) {
            event.setCancelled(true);
        }
    }

}
