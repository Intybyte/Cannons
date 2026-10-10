package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.CraftCannonFinderHelper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.craft.SubCraft;
import net.countercraft.movecraft.craft.datatag.CraftDataTagKey;
import net.countercraft.movecraft.craft.datatag.CraftDataTagRegistry;
import net.countercraft.movecraft.util.Pair;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.countercraft.movecraft.craft.type.TypeData.NUMERIC_PREFIX;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MovecraftUtils {

    private static final CraftDataTagKey<Set<Cannon>> CRAFT_CANNONS = CraftDataTagRegistry.INSTANCE.registerTagKey(new NamespacedKey(Cannons.getPlugin(), "movecraft_cannons"), CraftCannonFinderHelper::findCannonsAboardCraft);

    public static Set<Cannon> getCannons(Craft craft) {
        // Note: Might be worth it adding a "lifetime" to the cached value and to re-scan for new cannons every now and then
        return craft.getDataTag(CRAFT_CANNONS);
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
