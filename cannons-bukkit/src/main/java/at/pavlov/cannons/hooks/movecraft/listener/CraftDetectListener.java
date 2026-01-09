package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.movecraft.MovecraftCannonTracker;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import at.pavlov.cannons.hooks.movecraft.type.MaxCannonsEntry;
import net.countercraft.movecraft.craft.Craft;
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
    private static final Cannons plugin = Cannons.getPlugin();

    @EventHandler
    public void onCraftDetect(CraftDetectEvent e) {
        Craft craft = e.getCraft();
        CraftType type = craft.getType();
        if (notifyError.contains(type))
            return;

        Set<?> maxCannons = getCannonProperty(type);
        // Sum up counts of each cannon design
        Set<Cannon> cannons = MovecraftUtils.getCannons(craft);
        Map<String, Integer> cannonCount = new HashMap<>();
        for (var cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignName().toLowerCase();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }
        printCannonCount(cannonCount);

        // Check designs against maxCannons
        int size = craft.getOrigBlockCount();
        for (var entry : maxCannons) {
            if (!(entry instanceof MaxCannonsEntry max)) {
                plugin.logSevere("maxCannons must be a set of MaxCannonsEntry.");
                continue;
            }

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

        MovecraftCannonTracker.setCannons(craft.getUUID(), cannons);
    }

    private void printCannonCount(Map<String, Integer> cannonCount) {
        for (var entry : cannonCount.entrySet()) {
            plugin.logDebug("Cannon found: " + entry.getKey() + " | " + entry.getValue());
        }
    }

    private Set<MaxCannonsEntry> getCannonProperty(CraftType type) {
        try {
            Object objectProperty = type.getObjectProperty(MAX_CANNONS);
            if (objectProperty instanceof Set<?> property) {
                return (Set<MaxCannonsEntry>) property;
            } else {
                throw new IllegalStateException("maxCannons must be a set.");
            }
        } catch (Exception exception) {
            notifyError.add(type);
            plugin.logSevere(
                    "Failed to get maxCannons property from craft " +
                            type.getStringProperty(CraftType.NAME) +
                            " maxCannons won't be applied. - Cause: " +
                            exception.getMessage()
            );
            return Set.of();
        }
    }
}
