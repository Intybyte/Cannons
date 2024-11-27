package at.pavlov.cannons.movecraft.listener;

import at.pavlov.cannons.API.CannonsAPI;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.movecraft.type.MaxCannonsEntry;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static at.pavlov.cannons.movecraft.type.MaxCannonsProperty.MAX_CANNONS;

public class CraftDetectListener implements Listener {
    private static final CannonsAPI cannonAPI = Cannons.getPlugin().getCannonsAPI();

    @EventHandler
    public void onCraftDetect(CraftDetectEvent e) {
        Craft craft = e.getCraft();
        if (!(craft instanceof PlayerCraft))
            return;

        var objectProperty = craft.getType().getObjectProperty(MAX_CANNONS);
        if (!(objectProperty instanceof Set<?> maxCannons))
            throw new IllegalStateException("MAX_CANNONS must be a set.");

        if (maxCannons.isEmpty())
            return; // Return if empty set to improve performance

        // Sum up counts of each cannon design
        Set<Cannon> cannons = cannonAPI.getCannons(craft);
        Map<String, Integer> cannonCount = new HashMap<>();
        for (var cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignName().toLowerCase();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }

        // Check designs against maxCannons
        int size = craft.getOrigBlockCount();
        for (var entry : maxCannons) {
            if (!(entry instanceof MaxCannonsEntry max))
                throw new IllegalStateException("MAX_CANNONS must be a set of MaxCannonsEntry.");

            var cannonName = max.getName();
            var count = cannonCount.get(cannonName.toLowerCase());
            if (count == null)
                continue;

            var result = max.detect(count, size);

            result.ifPresent( error -> {
                e.setCancelled(true);
                e.setFailMessage("Detection Failed! You have too many cannons of the following type on this craft: "
                        + cannonName + ": " + error);
            });
        }
    }
}
