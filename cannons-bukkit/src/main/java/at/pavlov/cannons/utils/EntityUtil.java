package at.pavlov.cannons.utils;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.internal.enums.BreakCause;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityUtil {

    /**
     * searches for destroyed cannons in the explosion event and removes cannons parts which can't be destroyed in an explosion.
     * @param blocklist list of blocks involved in the event
     */
    public static void handleExplosionEvent(List<Block> blocklist){
        HashSet<UUID> remove = new HashSet<>();
        CannonManager cannonManager = CannonManager.getInstance();

        // first search if a barrel block was destroyed.
        for (Block block : blocklist) {
            Cannon cannon = cannonManager.getCannon(block.getLocation(), null);

            // if it is a cannon block
            if (cannon == null) {
                continue;
            }

            if (cannon.isDestructibleBlock(block.getLocation())) {
                //this cannon is destroyed
                remove.add(cannon.getUID());
            }
        }

        //iterate again and remove all block of intact cannons
        for (int i = 0; i < blocklist.size(); i++) {
            Block block = blocklist.get(i);
            Cannon cannon = cannonManager.getCannon(block.getLocation(), null);

            // if it is a cannon block and the cannon is not destroyed (see above)
            if (cannon == null || remove.contains(cannon.getUID())) {
                continue;
            }

            if (cannon.isCannonBlock(block)) {
                blocklist.remove(i--);
            }
        }

        //now remove all invalid cannons
        for (UUID id : remove)
            cannonManager.removeCannon(id, false, true, BreakCause.EXPLOSION);
    }
}
