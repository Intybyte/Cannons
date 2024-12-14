package at.pavlov.cannons.event;

import at.pavlov.bukkit.projectile.BukkitProjectile;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ProjectileImpactEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private BukkitProjectile projectile;
    private Location impactLocation;
    private final UUID shooter;
    private boolean cancelled;

    public ProjectileImpactEvent(BukkitProjectile projectile, Location impactLocation, UUID shooter) {
        this.projectile = projectile;
        this.impactLocation = impactLocation;
        this.shooter = shooter;
        this.cancelled = false;
    }

    public UUID getShooterUID() {
        return this.shooter;
    }

    public Location getImpactLocation() {
        return this.impactLocation;
    }

    public void setImpactLocation(Location impactLocation) {
        this.impactLocation = impactLocation;
    }

    public BukkitProjectile getProjectile() {
        return this.projectile;
    }

    public void setProjectile(BukkitProjectile projectile) {
        this.projectile = projectile;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
