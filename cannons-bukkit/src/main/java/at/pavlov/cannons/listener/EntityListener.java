package at.pavlov.cannons.listener;

import at.pavlov.cannons.Aiming;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.projectile.ProjectileManager;
import at.pavlov.cannons.utils.EntityUtil;
import at.pavlov.internal.enums.BreakCause;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityListener implements Listener
{
	private final Cannons plugin;
	private final CannonManager cannonManager;
	
	public EntityListener(Cannons plugin) {
		this.plugin = plugin;
		this.cannonManager = CannonManager.getInstance();
	}

	/**
	 * The projectile has hit an entity
	 * @param event
	 */
	@EventHandler
	public void onEntiyDeathEvent(EntityDeathEvent event) {
		Aiming.getInstance().removeTarget(event.getEntity());
	}


    /**
     * The projectile has hit an entity
     * @param event
     */
	@EventHandler
	public void onProjectileHitEntity(ProjectileHitEvent event) {
		ProjectileManager pm = ProjectileManager.getInstance();
		Projectile p = event.getEntity();
		if(p.getShooter() != null) {
			pm.directHitProjectile(p, event.getHitEntity());
		}

		pm.detonateProjectile(p);
	}

    /**
     * The projectile explosion has damaged an entity
     * @param event
     */
    @EventHandler
    public void onEntityDamageByBlockEvent(EntityDamageByBlockEvent event)
    {
        //if (plugin.getProjectileManager().isFlyingProjectile(event.getDamager()))
        {
            //event.setCancelled(true);
            //plugin.logDebug("Explosion damage was canceled. Damage done: " + event.getDamage());
        }
    }

	/**
	 * handles the explosion event. Protects the buttons and torches of a cannon, because they break easily
	 * @param event
	 */
	@EventHandler
	public void EntityExplode(EntityExplodeEvent event)
	{
		plugin.logDebug("Explode event listener called");

		//do nothing if it is cancelled
		if (event.isCancelled())
			return;

		EntityUtil.handleExplosionEvent(event.blockList());
	}
}
