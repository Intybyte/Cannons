package at.pavlov.cannons.hooks;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.util.MathUtils;
import net.countercraft.movecraft.util.hitboxes.BitmapHitBox;
import net.countercraft.movecraft.util.hitboxes.MutableHitBox;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class CraftCannonFinderHelper {

    public static Set<Cannon> findCannonsAboardCraft(final Craft craft) {
        return findCannonsAboardCraft(craft, HashSet::new, null);
    }

    public static Set<Cannon> findCannonsAboardCraft(final Craft craft, Supplier<Set<Cannon>> resultConstructor, final @Nullable MutableHitBox cannonBlocks) {
        final BitmapHitBox hitBox = new BitmapHitBox(craft.getHitBox());
        final UUID worldUID = craft.getMovecraftWorld().getWorldUUID();
        final Set<Cannon> result = resultConstructor.get();
        for (Cannon cannon : CannonManager.getCannonList().values()) {
            // Not even in the same world
            if (!worldUID.equals(cannon.getWorld())) {
                continue;
            }
            List<MovecraftLocation> cannonBlocksTmp = new ArrayList<>(cannon.getCannonDesign().getAllCannonBlocks(cannon).size());

            if (checkForCannonIntersection(cannon, hitBox, cannonBlocksTmp)) {
                if (cannonBlocks != null) {
                    cannonBlocks.addAll(cannonBlocksTmp);
                }
                result.add(cannon);
            }
        }
        return result;
    }

    /*
     * Goes over all blocks of the cannon, as soon as at least one block intersects with the hitbox, this returns true
     * All blocks of the cannon are added to cannonBlockCollection, regardless if they have been added or not
     * All positions of the cannon are always walked over!
     */
    private static boolean checkForCannonIntersection(final Cannon cannon, final MutableHitBox hitBox, List<MovecraftLocation> cannonBlockCollection) {
        boolean result = false;
        for (Location cannonBlock : cannon.getCannonDesign().getAllCannonBlocks(cannon)) {
            MovecraftLocation movecraftLocation = MathUtils.bukkit2MovecraftLoc(cannonBlock);
            cannonBlockCollection.add(movecraftLocation);

            if (result || !hitBox.inBounds(movecraftLocation) || !hitBox.contains(movecraftLocation)) {
                continue;
            }

            result = true;
        }
        return result;
    }

}
