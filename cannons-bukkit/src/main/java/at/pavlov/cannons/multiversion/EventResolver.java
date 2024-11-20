package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.Cannons;
import org.bukkit.ExplosionResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.lang.reflect.Constructor;
import java.util.List;

public class EventResolver {
    private static final int[] version = VersionHandler.getVersion();

    private EventResolver() {}

    public static EntityExplodeEvent getEntityExplodeEvent(
            Entity proj_entity,
            Location impactLoc,
            List<Block> blocks,
            float yield
    ) {
        if (version[1] >= 21) {
            return new EntityExplodeEvent(proj_entity, impactLoc, blocks, yield, ExplosionResult.DESTROY);
        } else {
            try {
                Constructor<?> constructor = EntityExplodeEvent.class.getConstructor(Entity.class, Location.class, List.class, float.class);
                return (EntityExplodeEvent) constructor.newInstance(proj_entity, impactLoc, blocks, yield);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }

        return null;
    }
}
