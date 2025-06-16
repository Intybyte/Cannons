package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftRotateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.Set;

public class RotationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void rotateListener(CraftRotateEvent e) {
        Craft craft = e.getCraft();

        Set<Cannon> cannons = MovecraftUtils.getCannons(craft);
        if (cannons.isEmpty())
            return;

        Vector v = e.getOriginPoint().toBukkit(craft.getWorld()).toVector();
        for (Cannon c : cannons) {
            switch (e.getRotation()) {
                case CLOCKWISE -> c.rotateRight(v);
                case ANTICLOCKWISE -> c.rotateLeft(v);
            }
        }
    }
}
