package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.SubCraft;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MovecraftUtils {

    public static Set<Cannon> getCannons(Craft craft) {
        List<Location> shipLocations = new ArrayList<>();
        for (MovecraftLocation loc : craft.getHitBox()) {
            shipLocations.add(loc.toBukkit(craft.getWorld()));
        }

        return CannonManager.getCannonsByLocations(shipLocations);
    }

    /**
     * This method tries to get the player that is piloting the craft, or if the craft
     * is a subcraft, the pilot of the parent craft.
     *
     * @param craft Movecraft craft to search for its pilot
     * @return UUID of the pilot
     */
    public static UUID getPlayerFromCraft(Craft craft) {
        if (craft instanceof PilotedCraft pilotedCraft) {
            // If this is a piloted craft, return the pilot's UUID
            return pilotedCraft.getPilot().getUniqueId();
        }

        if (craft instanceof SubCraft subCraft) {
            // If this is a subcraft, look for a parent
            Craft parent = subCraft.getParent();
            if (parent != null) {
                // If the parent is not null, recursively check it for a UUID
                return getPlayerFromCraft(parent);
            }
        }

        // Return null if all else fails
        return null;
    }
}
