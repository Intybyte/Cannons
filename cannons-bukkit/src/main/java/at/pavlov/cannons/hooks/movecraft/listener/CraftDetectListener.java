package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
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

    @EventHandler(ignoreCancelled = true)
    public void onCraftDetect(CraftDetectEvent e) {
        Craft craft = e.getCraft();
        CraftType type = craft.getType();

        if (!(craft instanceof PlayerCraft)) return;

        // Sum up counts of each cannon design
        Set<Cannon> cannons = MovecraftUtils.getCannons(craft);

        if (cannons.isEmpty()) return;

        if (checkMaxMin(e, type, cannons, craft)) return;

        if (checkMass(e, cannons, type)) return;

        boolean useShip = CannonProperties.USE_SHIP_ANGLES.get(type) == Boolean.TRUE;
        if (useShip) {
            cannons.forEach(it -> it.setOnShip(true));
        }
    }

    private boolean checkMass(CraftDetectEvent e, Set<Cannon> cannons, CraftType type) {
        int cannonsMassCount = 0;
        Set<String> exclude = CannonProperties.EXCLUDE_FROM_MASS.get(type);
        for (Cannon cannon : cannons) {
            CannonDesign design = cannon.getCannonDesign();

            if (!exclude.contains(design.getDesignID())) {
                cannonsMassCount += design.getMassOfCannon();
            }
        }

        cannon.logDebug("MassCount " + cannonsMassCount);
        Integer maxMass = CannonProperties.MAX_MASS.get(type);
        if (maxMass != null && maxMass < cannonsMassCount) {
            e.setCancelled(true);
            e.setFailMessage(
                String.format(
                    "Detection Failed! Too much cannon mass on board! %d > %d", cannonsMassCount, maxMass
                )
            );
            return true;
        }

        Integer minMass = CannonProperties.MIN_MASS.get(type);
        if (minMass != null && minMass > cannonsMassCount) {
            e.setCancelled(true);
            e.setFailMessage(
                String.format(
                    "Detection Failed! Not enough cannon mass on board! %d < %d", cannonsMassCount, minMass
                )
            );
            return true;
        }

        return false;
    }

    private boolean checkMaxMin(CraftDetectEvent e, CraftType type, Set<Cannon> cannons, Craft craft) {
        Set<? extends CannonCheck> cannonMaxMinChecks = CannonProperties.MAX_CANNONS.get(type);
        cannonMaxMinChecks.addAll(CannonProperties.MIN_CANNONS.get(type));

        if (cannonMaxMinChecks.isEmpty()) return false;

        Map<String, Integer> cannonCount = new HashMap<>();
        for (Cannon cannon : cannons) {
            String design = cannon.getCannonDesign().getDesignID();
            cannonCount.compute(design, (key, value) -> (value == null) ? 1 : value + 1);
        }

        for (var entry : cannonCount.entrySet()) {
            cannon.logDebug("Cannon found: " + entry.getKey() + " | " + entry.getValue());
        }

        for (CannonCheck check : cannonMaxMinChecks) {
            Optional<String> result = check.check(craft, cannonCount);

            if (result.isEmpty()) continue;

            String error = result.get();
            e.setCancelled(true);
            e.setFailMessage("Detection Failed! " + error);
            return true;
        }
        return false;
    }
}
