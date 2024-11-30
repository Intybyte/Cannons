package at.pavlov.cannons.listener;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.Enum.BreakCause;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.projectile.ProjectileManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityListener implements Listener
{
	private final Cannons plugin;
	
	public EntityListener(Cannons plugin)
	{
		this.plugin = plugin;
	}

	/**
	 * The projectile has hit an entity
	 * @param event
	 */
	@EventHandler
	public void onEntiyDeathEvent(EntityDeathEvent event) {
		plugin.getAiming().removeTarget(event.getEntity());
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
		
		ExplosionEventHandler(event.blockList());
	}

    /**
     * searches for destroyed cannons in the explosion event and removes cannons parts which can't be destroyed in an explosion.
     * @param blocklist list of blocks involved in the event
     */
    public void ExplosionEventHandler(List<Block> blocklist){
        HashSet<UUID> remove = new HashSet<>();

        // first search if a barrel block was destroyed.
        for (Block block : blocklist) {
            Cannon cannon = plugin.getCannonManager().getCannon(block.getLocation(), null);

            // if it is a cannon block
            if (cannon == null) {
                continue;
            }

            if (cannon.isDestructibleBlock(block.getLocation())) {
                //this cannon is destroyed
                remove.add(cannon.getUID());
            }
        }

        //iterate again and remove all block of intact cannons
        for (int i = 0; i < blocklist.size(); i++) {
            Block block = blocklist.get(i);
            Cannon cannon = plugin.getCannonManager().getCannon(block.getLocation(), null);

            // if it is a cannon block and the cannon is not destroyed (see above)
            if (cannon == null || remove.contains(cannon.getUID())) {
                continue;
            }

            if (cannon.isCannonBlock(block)) {
                blocklist.remove(i--);
            }
        }

        //now remove all invalid cannons
        for (UUID id : remove)
            plugin.getCannonManager().removeCannon(id, false, true, BreakCause.Explosion);
    }
}
