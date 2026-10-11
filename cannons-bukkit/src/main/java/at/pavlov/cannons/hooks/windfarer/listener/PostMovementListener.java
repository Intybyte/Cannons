package at.pavlov.cannons.hooks.windfarer.listener;

import at.pavlov.cannons.hooks.windfarer.datatag.CraftCannonsData;
import net.countercraft.movecraft.MovecraftRotation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.NullCraft;
import net.countercraft.movecraft.craft.SinkingCraft;
import net.countercraft.movecraft.events.CraftFinishMovementEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PostMovementListener implements Listener {

    @EventHandler
    public void onAfterTranslate(CraftFinishMovementEvent event) {
        final Craft craft = event.getCraft();
        if (craft instanceof SinkingCraft || craft instanceof NullCraft) {
            return;
        }
        final CraftCannonsData cannonsData = CraftCannonsData.of(craft);
        if (event.getRotation() != MovecraftRotation.NONE) {
            cannonsData.processRotation(event.getRotation(), event.getRotationOrigin(), event.getCraft());
        }
        else {
            int translation = Math.abs(event.getDx()) + Math.abs(event.getDy()) + Math.abs(event.getDz());
            if (translation > 0 || !event.getOldWorld().equals(event.getCraft().getWorld().getUID())) {
                cannonsData.processTranslation(event.getDx(), event.getDy(), event.getDz(), event.getOldWorld(), event.getCraft());
            }
        }
    }

}
