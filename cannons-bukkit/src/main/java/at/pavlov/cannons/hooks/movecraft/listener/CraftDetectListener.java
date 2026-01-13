package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import at.pavlov.cannons.hooks.movecraft.type.CannonCheck;
import at.pavlov.cannons.hooks.movecraft.type.properties.CannonProperties;
import at.pavlov.cannons.hooks.movecraft.type.properties.PropertyWrapper;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PlayerCraft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftDetectEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CraftDetectListener implements Listener {
    private static final Cannons cannon = Cannons.getPlugin();

    @EventHandler
    public void onCraftDetect(CraftDetectEvent e) {
        Craft craft = e.getCraft();
        CraftType type = craft.getType();

        if (PropertyWrapper.notifyError.contains(type)) return;

        if (!(craft instanceof PlayerCraft)) return;

        Set<? extends CannonCheck> cannonMaxMinChecks = CannonProperties.MAX_CANNONS.get(type);
        cannonMaxMinChecks.addAll(CannonProperties.MIN_CANNONS.get(type));

        if (cannonMaxMinChecks.isEmpty()) return;

        // Sum up counts of each cannon design
        Set<Cannon> cannons = MovecraftUtils.getCannons(craft);

        Map<String, Integer> cannonCount = new HashMap<>();
        for (Cannon cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignID();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }

        printCannonCount(cannonCount);

        for (CannonCheck check : cannonMaxMinChecks) {
            Optional<String> result = check.check(craft, cannonCount);

            if (result.isPresent()) {
                String error = result.get();
                e.setCancelled(true);
                e.setFailMessage("Detection Failed! " + error);
                return;
            }
        }

        int cannonsMassCount = 0;
        for (Cannon cannon : cannons) {
            cannonsMassCount += cannon.getCannonDesign().getMassOfCannon();
        }

        Integer maxMass = CannonProperties.MAX_MASS.get(type);
        if (maxMass != null && maxMass < cannonsMassCount) {
            e.setCancelled(true);
            e.setFailMessage(
                String.format(
                    "Detection Failed! Too much cannon mass on board! %d > %d", cannonsMassCount, maxMass
                )
            );
            return;
        }

        Integer minMass = CannonProperties.MIN_MASS.get(type);
        if (minMass != null && minMass > cannonsMassCount) {
            e.setCancelled(true);
            e.setFailMessage(
                String.format(
                    "Detection Failed! Not enough cannon mass on board! %d < %d", cannonsMassCount, minMass
                )
            );
        }
    }

    private void printCannonCount(Map<String, Integer> cannonCount) {
        for (var entry : cannonCount.entrySet()) {
            cannon.logDebug("Cannon found: " + entry.getKey() + " | " + entry.getValue());
        }
    }
}
