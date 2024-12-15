package at.pavlov.bukkit.container;

import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.internal.container.FakeBlockEntry;
import at.pavlov.internal.enums.FakeBlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitFakeBlockEntry extends FakeBlockEntry {

    public BukkitFakeBlockEntry(Location loc, Player player, FakeBlockType type, long duration) {
        super(VectorUtils.fromLoc(loc), loc.getWorld().getUID(), player.getUniqueId(), type, duration);
    }

    public World getWorldBukkit() {
        return Bukkit.getWorld(getWorld());
    }

    public Location getLocation() {
        World world = getWorldBukkit();
        if (world == null) {
            return null;
        }

        return new Location(world, getLocX(), getLocY(), getLocZ());
    }

    public Player getPlayerBukkit() {
        return Bukkit.getPlayer(getPlayer());
    }
}
