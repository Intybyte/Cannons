package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.util.Pair;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static net.countercraft.movecraft.craft.type.TypeData.NUMERIC_PREFIX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static @NotNull Pair<Boolean, ? extends Number> parseLimit(@NotNull Object input) {
        if (!(input instanceof String str)) {
            return new Pair<>(false, (double) input);
        }

        if (!str.contains(NUMERIC_PREFIX)) {
            return new Pair<>(false, Double.valueOf(str));
        }

        String[] parts = str.split(NUMERIC_PREFIX);
        int val = Integer.parseInt(parts[1]);
        return new Pair<>(true, val);
    }

    public static @NotNull List<@NotNull String> parseKey(Object key) {
        if (key instanceof String string) {
            return List.of(string);
        } else if (key instanceof ArrayList<?> array) {
            if (array.get(0) instanceof String) {
                return (ArrayList<String>) array;
            }

            throw new IllegalArgumentException("Invalid parsed key");
        } else {
            throw new IllegalArgumentException("Invalid parsed key");
        }
    }
}
