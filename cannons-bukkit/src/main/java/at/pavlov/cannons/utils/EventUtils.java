package at.pavlov.cannons.utils;

import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EventUtils {
    private EventUtils() {}

    /**
     * searches for destroyed cannons in the explosion event and removes cannons parts which can't be destroyed in an explosion.
     * @param blocklist list of blocks involved in the event
     */
    public static void handleExplosion(List<Block> blocklist) {
        CannonManager cannonManager = CannonManager.getInstance();
        HashSet<UUID> remove = new HashSet<>();
        HashMap<Block, Cannon> cannonHashMap = new HashMap<>();

        // first search if a barrel block was destroyed.
        for (Block block : blocklist) {
            if (block == null) continue;

            Location location = block.getLocation();
            if (block.getType().isAir()) continue;

            Cannon cannon = cannonManager.getCannonFromStorage(location);
            // if it is a cannon block
            if (cannon == null) {
                continue;
            }

            cannonHashMap.put(block, cannon);
            if (cannon.isDestructibleBlock(location)) {
                //this cannon is destroyed
                remove.add(cannon.getUID());
            }
        }

        //iterate again and remove all block of intact cannons
        for (int i = 0; i < blocklist.size(); i++) {
            Block block = blocklist.get(i);
            Cannon cannon = cannonHashMap.get(block);

            // if it is a cannon block and the cannon is not destroyed (see above)
            if (cannon == null || remove.contains(cannon.getUID())) {
                continue;
            }

            if (cannon.isCannonBlock(block)) {
                blocklist.remove(i--);
            }
        }

        //now remove all invalid cannons
        for (UUID id : remove) {
            cannonManager.removeCannon(id, false, true, BreakCause.Explosion);
        }
    }
}
