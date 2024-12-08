package at.pavlov.bukkit.cannons.holders;

import at.pavlov.internal.cannons.holders.CannonPositionHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public interface CannonPositionHBukkit extends CannonPositionHolder<BlockFace, Vector> {

    /**
     * @return bukkit world from saved UUID
     */
    default World getWorldBukkit() {
        if (this.getWorld() == null) {
            return null;
        }
        World bukkitWorld = Bukkit.getWorld(this.getWorld());
        //if (bukkitWorld == null)
        //    Cannons.logger().info("Can't find world: " + getWorld());
        return Bukkit.getWorld(this.getWorld());
        // return new Location(bukkitWorld, )
    }

    default void move(Vector moved) {
        getOffset().add(moved);
        this.hasUpdated();
    }

    Location getLocation();

    Location getMuzzle();
}
