package at.pavlov.cannons.event;

import at.pavlov.cannons.projectile.FlyingProjectile;
import at.pavlov.cannons.projectile.Projectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Getter
@Setter
public class ProjectileImpactEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final FlyingProjectile flyingProjectile;
    private final Location impactLocation;
    private boolean cancelled;

    public ProjectileImpactEvent(FlyingProjectile flyingProjectile, Location impactLocation) {
        this.flyingProjectile = flyingProjectile;
        this.impactLocation = impactLocation;
        this.cancelled = false;
    }

    /**
     * Use ProjectileImpactEvent#getShooter instead
     */
    @Deprecated
    public UUID getShooterUID() {
        return this.flyingProjectile.getShooterUID();
    }

    /**
     * Use ProjectileImpactEvent#getFlyingProjectile
     */
    @Deprecated
    public Projectile getProjectile() {
        return flyingProjectile.getProjectile();
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
