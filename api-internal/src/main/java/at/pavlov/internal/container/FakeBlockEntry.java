package at.pavlov.internal.container;

import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.enums.FakeBlockType;
import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class FakeBlockEntry {
    protected final int locX;
    protected final int locY;
    protected final int locZ;
    protected final UUID world;

    protected long startTime;
    //how long the block stays in ticks
    protected final long duration;

    //fake block will only be shown to this player
    protected final UUID player;
    //only one type effect will be shown (aiming, explosion,...)
    protected final FakeBlockType type;

    public FakeBlockEntry(CannonVector loc, UUID world, UUID player, FakeBlockType type, long duration) {
        this.locX = loc.getBlockX();
        this.locY = loc.getBlockY();
        this.locZ = loc.getBlockZ();
        this.world = world;

        this.player = player;
        this.type = type;

        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public void updateTime() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return (System.currentTimeMillis() > startTime + duration * 50);
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
