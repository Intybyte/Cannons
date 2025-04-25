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
    private final UUID shooter;
    private boolean cancelled;

    public ProjectileImpactEvent(FlyingProjectile flyingProjectile, Location impactLocation, UUID shooter) {
        this.flyingProjectile = flyingProjectile;
        this.impactLocation = impactLocation;
        this.shooter = shooter;
        this.cancelled = false;
    }

    /**
     * Use ProjectileImpactEvent#getShooter instead
     */
    @Deprecated
    public UUID getShooterUID() {
        return this.shooter;
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
