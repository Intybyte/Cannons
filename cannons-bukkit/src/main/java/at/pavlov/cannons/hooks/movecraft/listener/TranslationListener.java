package at.pavlov.cannons.hooks.movecraft.listener;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.movecraft.MovecraftCannonTracker;
import at.pavlov.cannons.hooks.movecraft.MovecraftUtils;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Set;

public class TranslationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void translateListener(CraftTranslateEvent e) {
        Vector v = delta(e);
        if (v == null)
            return;

        Set<Cannon> cannons = MovecraftCannonTracker.getCannons(e.getCraft().getUUID());
        if (cannons == null) return; //for sinking ships

        if (cannons.isEmpty())
            return;
        for (Cannon c : cannons) {
            c.move(v);
        }
    }

    @Nullable
    private Vector delta(@NotNull CraftTranslateEvent e) {
        if (e.getOldHitBox().isEmpty() || e.getNewHitBox().isEmpty())
            return null;

        MovecraftLocation oldMid = e.getOldHitBox().getMidPoint();
        MovecraftLocation newMid = e.getNewHitBox().getMidPoint();

        int dx = newMid.getX() - oldMid.getX();
        int dy = newMid.getY() - oldMid.getY();
        int dz = newMid.getZ() - oldMid.getZ();

        return new Vector(dx, dy, dz);
    }
}
