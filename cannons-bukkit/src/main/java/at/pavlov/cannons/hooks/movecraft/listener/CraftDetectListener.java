package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
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

import static at.pavlov.cannons.hooks.movecraft.type.MaxCannonsProperty.MAX_CANNONS;

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

        Object objectProperty;
        Set<?> maxCannons;
        try {
            objectProperty = craft.getType().getObjectProperty(MAX_CANNONS);
            if (objectProperty instanceof Set<?> objects) {
                maxCannons = objects;
            } else {
                throw new IllegalStateException("MAX_CANNONS must be a set.");
            }
        } catch (IllegalStateException exception) {
            notifyError.add(type);
            cannon.logSevere(
                "Failed to get max_cannons property from craft " +
                type.getStringProperty(CraftType.NAME) +
                " max_cannons won't be applied. - Cause: " +
                e.getFailMessage()
            );
            return;
        }

        if (maxCannons.isEmpty())
            return; // Return if empty set to improve performance

        // Sum up counts of each cannon design
        Set<Cannon> cannons = cannon.getCannonsAPI().getCannons(craft);
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
