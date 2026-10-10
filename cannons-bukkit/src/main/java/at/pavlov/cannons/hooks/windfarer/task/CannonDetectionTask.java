package at.pavlov.cannons.hooks.windfarer.task;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.windfarer.datatag.CraftCannonsData;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.processing.effects.Effect;

import java.util.function.Supplier;

public class CannonDetectionTask implements Supplier<Effect> {

    protected final Craft craft;

    public CannonDetectionTask(Craft craft) {
        this.craft = craft;
    }

    @Override
    public Effect get() {
        // Iterate over all positions aboard the craft multithreaded
        // Attempt to check if there is a cannon there
        // Access the cannons object
        // Add the cannons to it

        final long startTime = System.currentTimeMillis();
        final CraftCannonsData cannonsData = CraftCannonsData.of(this.craft);
        // DONE: New approach: Do it "backwards" => Go through all cannons and check them against the hitbox! Not the the current "Go through every hitbox location and check if there is a cannon"
        // Cannons maintainer refuses to change the cannon cache to something faster like a Map<ChunkPos, Set<Cannon>> cache which would reduce lookup times in general greatly
        if (cannonsData.findCannonsAboardCraft(this.craft)) {
            // Print out, how many cannons we have detected
            final long timeTaken = System.currentTimeMillis() - startTime;
            Cannons.getPlugin().logDebug("Successfully detected <" + cannonsData.getCannonCount() + "> aboard Craft <" + craft.getUUID().toString() + "> in " + timeTaken + "ms !");
        }

        // We dont have a effect that we will run, so return null!
        return null;
    }

}
