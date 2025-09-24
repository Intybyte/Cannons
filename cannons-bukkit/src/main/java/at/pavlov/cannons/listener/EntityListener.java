package at.pavlov.cannons.listener;

import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.multiversion.EventResolver;
import at.pavlov.cannons.projectile.ProjectileManager;
import at.pavlov.cannons.utils.EventUtils;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityListener implements Listener {
    private final Cannons plugin;

    public EntityListener(Cannons plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntiyDeathEvent(EntityDeathEvent event) {
        Aiming.getInstance().removeTarget(event.getEntity());
    }

    @EventHandler
    public void onProjectileHitEntity(ProjectileHitEvent event) {
        ProjectileManager pm = ProjectileManager.getInstance();
        Projectile p = event.getEntity();
        if (p.getShooter() != null) {
            pm.directHitProjectile(p, event.getHitEntity());
        }

        pm.detonateProjectile(p);
    }

    /**
     * handles the explosion event. Protects the buttons and torches of a cannon, because they break easily
     * @param event
     */
    @EventHandler(ignoreCancelled = true)
    public void EntityExplode(EntityExplodeEvent event) {
        plugin.logDebug("Explode event listener called");

        if (!EventResolver.isValidExplosion(event)) {
            return;
        }

        event.blockList().removeIf(it -> it.getType().isAir());
        EventUtils.handleExplosion(event.blockList());
    }
}
