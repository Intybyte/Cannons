package at.pavlov.cannons.hooks.windfarer.listener;

import at.pavlov.cannons.hooks.windfarer.task.CannonDetectionTask;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.NullCraft;
import net.countercraft.movecraft.craft.SinkingCraft;
import net.countercraft.movecraft.events.CraftGatherAdditionalDetectionStepsEvent;
import net.countercraft.movecraft.processing.effects.Effect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.function.Supplier;

public class RegisterDetectionStepsListener implements Listener {

    // Low so this fires after windfarer => Windfarer's sign detection needs to run first
    @EventHandler(priority = EventPriority.LOW)
    public void onDetectAddSteps(final CraftGatherAdditionalDetectionStepsEvent event) {
        // Cannon detection
        // Can return null, if that step is not to be run for that craft!
        event.addStep(RegisterDetectionStepsListener::cannonDetectionStepBuilder);
    }

    private static Supplier<Effect> cannonDetectionStepBuilder(Supplier<Effect> effectSupplier, Craft craft) {
        // Invalid crafts
        if (craft instanceof SinkingCraft || craft instanceof NullCraft) {
            return null;
        }
        return new CannonDetectionTask(craft);
    }


}
