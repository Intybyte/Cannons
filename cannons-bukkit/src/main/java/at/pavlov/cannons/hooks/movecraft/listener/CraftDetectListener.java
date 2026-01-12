package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import at.pavlov.cannons.hooks.movecraft.type.CannonProperties;
import at.pavlov.cannons.hooks.movecraft.type.MaxCannonsEntry;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CraftDetectListener implements Listener {
    private static final Set<CraftType> notifyError = new HashSet<>();
    private static final Cannons cannon = Cannons.getPlugin();

    @EventHandler
    public void onCraftDetect(CraftDetectEvent e) {
        Craft craft = e.getCraft();
        CraftType type = craft.getType();
        if (notifyError.contains(type))
            return;

        if (!(craft instanceof PlayerCraft))
            return;

        Set<MaxCannonsEntry> maxCannons = CannonProperties.MAX_CANNONS.get(type);
        if (maxCannons.isEmpty())
            return; // Return if empty set to improve performance

        // Sum up counts of each cannon design
        Set<Cannon> cannons = MovecraftUtils.getCannons(craft);
        Map<String, Integer> cannonCount = new HashMap<>();
        for (var cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignName().toLowerCase();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }
        printCannonCount(cannonCount);

        // Check designs against maxCannons
        for (var max : maxCannons) {
            var cannonName = max.getName();
            var count = cannonCount.get(cannonName.toLowerCase());
            if (count == null)
                continue;

            var result = max.check(craft, cannonCount);

            result.ifPresent( error -> {
                e.setCancelled(true);
                e.setFailMessage("Detection Failed! " + error);
            });
        }
    }

    private void printCannonCount(Map<String, Integer> cannonCount) {
        for (var entry : cannonCount.entrySet()) {
            cannon.logDebug("Cannon found: " + entry.getKey() + " | " + entry.getValue());
        }
    }
}
