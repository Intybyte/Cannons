package at.pavlov.cannons.container;

import at.pavlov.internal.enums.FakeBlockType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class FakeBlockEntry {
    private final int locX;
    private final int locY;
    private final int locZ;
    private final UUID world;

    private long startTime;
    //how long the block stays in ticks
    private final long duration;

    //fake block will only be shown to this player
    private final UUID player;
    //only one type effect will be shown (aiming, explosion,...)
    private final FakeBlockType type;

    public FakeBlockEntry(Location loc, Player player, FakeBlockType type, long duration) {
        this.locX = loc.getBlockX();
        this.locY = loc.getBlockY();
        this.locZ = loc.getBlockZ();
        this.world = loc.getWorld().getUID();

        this.player = player.getUniqueId();
        this.type = type;

        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }


    public World getWorldBukkit() {
        return Bukkit.getWorld(getWorld());
    }

    public Location getLocation() {
        World world = getWorldBukkit();
        if (world != null)
            return new Location(world, getLocX(), getLocY(), getLocZ());
        else
            return null;
    }

    public void updateTime() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() > getStartTime() + getDuration() * 50);
    }

    public Player getPlayerBukkit() {
        return Bukkit.getPlayer(getPlayer());
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash = 31 * hash + (this.world != null ? this.world.hashCode() : 0);
        hash = 31 * hash + Integer.hashCode(this.locX);
        hash = 31 * hash + Integer.hashCode(this.locY);
        hash = 31 * hash + Integer.hashCode(this.locZ);
        hash = 31 * hash + player.hashCode();
        hash = 31 * hash + type.hashCode();
        return hash;
    }

    public boolean samePosition(FakeBlockEntry entry) {
        return this.locX == entry.getLocX() && this.locY == entry.getLocY() && this.locZ == entry.getLocZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FakeBlockEntry entry)) {
            return false;
        }

        return this.type == entry.getType()
                && this.samePosition(entry)
                && this.world.equals(entry.getWorld())
                && this.player.equals(entry.getPlayer());
    }

}
