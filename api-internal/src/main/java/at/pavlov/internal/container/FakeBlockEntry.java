package at.pavlov.internal.container;

import at.pavlov.internal.enums.FakeBlockType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class FakeBlockEntry implements Cloneable {
    private final int locX;
    private final int locY;
    private final int locZ;
    private final UUID world;
    //how long the block stays in ticks
    private final long duration;
    //fake block will only be shown to this player
    private final UUID player;
    //only one type effect will be shown (aiming, explosion,...)
    private final FakeBlockType type;
    private long startTime;


    public FakeBlockEntry(int locX, int locY, int locZ, UUID world, UUID player, FakeBlockType type, long duration) {
        this.locX = locX;
        this.locY = locY;
        this.locZ = locZ;
        this.world = world;

        this.player = player;
        this.type = type;

        this.startTime = System.currentTimeMillis();
        this.duration = duration;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
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

    @Override
    public boolean equals(Object obj) {
        FakeBlockEntry obj2 = (FakeBlockEntry) obj;
        return this.type == obj2.getType() && this.locX == obj2.getLocX() && this.locY == obj2.getLocY() && this.locZ == obj2.getLocZ() && this.world.equals(obj2.getWorld()) && this.player.equals(obj2.getPlayer());
    }

    @Override
    public FakeBlockEntry clone() {
        try {
            return (FakeBlockEntry) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
