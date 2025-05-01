package at.pavlov.cannons.event;

import at.pavlov.cannons.projectile.FlyingProjectile;
import at.pavlov.cannons.projectile.Projectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
@Setter
public class ProjectilePiercingEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final FlyingProjectile flyingProjectile;
    private final Location impactLocation;
    private List<Block> blockList;
    private boolean cancelled;

    public ProjectilePiercingEvent(FlyingProjectile flyingProjectile, Location impactLocation, List<Block> blockList) {
        this.flyingProjectile = flyingProjectile;
        this.impactLocation = impactLocation;
        this.blockList = blockList;
        this.cancelled = false;
    }


    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Deprecated
    public Projectile getProjectile() {
        return this.flyingProjectile.getProjectile();
    }
}
