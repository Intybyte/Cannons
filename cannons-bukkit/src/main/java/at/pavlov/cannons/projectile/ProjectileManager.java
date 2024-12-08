package at.pavlov.cannons.projectile;

import at.pavlov.bukkit.projectile.Projectile;
import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.CreateExplosion;
import at.pavlov.internal.enums.ProjectileCause;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.cannons.dao.DelayedTask;
import org.apache.commons.lang3.Validate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileManager
{
    private static ProjectileManager instance = null;

    private final Cannons plugin;
    private final ConcurrentHashMap<UUID, FlyingProjectile> flyingProjectilesMap = new ConcurrentHashMap<>();

    public static void initialize(Cannons plugin) {
        if (instance != null)
            return;

        instance = new ProjectileManager(plugin);
    }

    public static ProjectileManager getInstance() {
        return instance;
    }

    /**
     * ProjectileManager
     * @param plugin - Cannons instance
     */
    private ProjectileManager(Cannons plugin) {
        this.plugin = plugin;
    }

    public org.bukkit.entity.Projectile spawnProjectile(Projectile projectile, UUID shooter, org.bukkit.projectiles.ProjectileSource source, Location playerLoc, Location spawnLoc, Vector velocity, UUID cannonId, ProjectileCause projectileCause)
    {
        Validate.notNull(shooter, "shooter for the projectile can't be null");
        World world = spawnLoc.getWorld();

        //set yaw, pitch for fireballs
        double v = velocity.length();
        spawnLoc.setPitch((float) (Math.acos(velocity.getY()/v)*180.0/Math.PI - 90));
        spawnLoc.setYaw((float) (Math.atan2(velocity.getZ(),velocity.getX())*180.0/Math.PI - 90));

        Entity pEntity = world.spawnEntity(spawnLoc, projectile.getProjectileEntity());

        //calculate firing vector
        pEntity.setVelocity(velocity);

        org.bukkit.entity.Projectile projectileEntity;
        try
        {
            projectileEntity = (org.bukkit.entity.Projectile) pEntity;
        }
        catch(Exception e)
        {
            plugin.logDebug("Can't convert EntityType " + pEntity.getType() + " to projectile. Using additional Snowball");
            projectileEntity = (org.bukkit.entity.Projectile) world.spawnEntity(spawnLoc, EntityType.SNOWBALL);
            projectileEntity.setVelocity(velocity);
        }

        if (projectile.isProjectileOnFire())
            projectileEntity.setFireTicks(100);
        //projectileEntity.setTicksLived(2);



        //create a new flying projectile container
        FlyingProjectile cannonball = new FlyingProjectile(projectile, projectileEntity, shooter, source, playerLoc, cannonId, projectileCause);


        flyingProjectilesMap.put(cannonball.getUID(), cannonball);

        //detonate timefused projectiles
        detonateTimefuse(cannonball);

        return projectileEntity;
    }



    /**
     * detonate a timefused projectile mid air
     * @param cannonball - the cannonball to detonate
     */
    private void detonateTimefuse(final FlyingProjectile cannonball)
    {
        if (cannonball.getProjectile().getTimefuse() <= 0) {
            return;
        }

        //Delayed Task
        AsyncTaskManager.get().scheduler.runTaskLater(new DelayedTask(cannonball.getUID())
        {
            public void run(Object object)
            {
                //find given UID in list
                FlyingProjectile fproj = flyingProjectilesMap.get(object);

                if(fproj != null) {
                    //detonate timefuse
                    org.bukkit.entity.Projectile projectile_entity = fproj.getProjectileEntity();
                    //the projectile might be null
                    if (projectile_entity != null) {
                        CreateExplosion.getInstance().detonate(cannonball, projectile_entity);
                        projectile_entity.remove();
                    }
                    flyingProjectilesMap.remove(cannonball.getUID());
                }
            }}, (long) (cannonball.getProjectile().getTimefuse()*20));
    }


    /**
     * detonates the given projectile entity
     * @param projectile - the projectile with this entity
     */
    public void detonateProjectile(Entity projectile)
    {
        if(projectile == null || !(projectile instanceof org.bukkit.entity.Projectile))
            return;

        FlyingProjectile fproj = flyingProjectilesMap.get(projectile.getUniqueId());
        if (fproj!=null)
        {
            CreateExplosion.getInstance().detonate(fproj, (org.bukkit.entity.Projectile) projectile);
            projectile.remove();
            flyingProjectilesMap.remove(fproj.getUID());
        }
    }

    /**
     * detonates the given projectile entity
     * @param cannonball - the projectile with this entity
     * @param target the entity hit by the projectile
     */
    public void directHitProjectile(Entity cannonball, Entity target) {
        if(cannonball == null || target == null) return;

        FlyingProjectile fproj = flyingProjectilesMap.get(cannonball.getUniqueId());
        if (fproj == null) {
            return;
        }

        org.bukkit.entity.Projectile projectile_entity = fproj.getProjectileEntity();
        if (!fproj.hasDetonated() && cannonball.isValid()) {
            fproj.setDetonated(true);
            CreateExplosion.getInstance().directHit(fproj, projectile_entity, target);
            projectile_entity.remove();
        }

        flyingProjectilesMap.remove(fproj.getUID());
    }

    /**
     * returns true if the given entity is a cannonball projectile
     * @param projectile flying projectile
     * @return true if cannonball projectile
     */
    public boolean isFlyingProjectile(Entity projectile)
    {
        FlyingProjectile fproj = flyingProjectilesMap.get(projectile.getUniqueId());
        return fproj != null;
    }


    /**
     * returns the list of all flying projectiles
     * @return - the list of all flying projectiles
     */
    public ConcurrentHashMap<UUID, FlyingProjectile> getFlyingProjectiles()
    {
        return flyingProjectilesMap;
    }

    /**
     * returns the projectile of which the player is passenger
     * if the player is attached to a projectile he will follow its movement
     * @param player is the passenger
     * @return the projectile or null
     */
    public FlyingProjectile getAttachedProjectile(Player player)
    {
        if (player == null) {
            return null;
        }

        for (FlyingProjectile proj : flyingProjectilesMap.values())
            if (proj.getShooterUID().equals(player.getUniqueId()))
                return proj;
        return null;
    }
}
