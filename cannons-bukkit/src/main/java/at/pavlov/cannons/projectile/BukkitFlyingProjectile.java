package at.pavlov.cannons.projectile;

import at.pavlov.bukkit.container.BukkitMovingObject;
import at.pavlov.bukkit.factory.CoordinateUtil;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.bukkit.projectile.BukkitProjectile;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import at.pavlov.internal.enums.ProjectileCause;
import at.pavlov.internal.projectile.FlyingProjectile;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.UUID;


public class BukkitFlyingProjectile extends FlyingProjectile<Projectile, BukkitProjectile, BukkitMovingObject, ProjectileSource> {

    public BukkitFlyingProjectile(BukkitProjectile projectile, Projectile projectile_entity, UUID shooterUID, org.bukkit.projectiles.ProjectileSource source, Location playerLoc, UUID cannonId, ProjectileCause projectileCause) {
        //Validate.notNull(shooterUID, "shooterUID for the projectile can't be null");
        this.entityUID = projectile_entity.getUniqueId();
        this.worldUID = projectile_entity.getWorld().getUID();

        this.wasInWater = this.isInWater();
        this.projectile = projectile;
        this.cannonUID = cannonId;
        this.shooterUID = shooterUID;
        this.playerlocation = CoordinateUtil.fromLoc(playerLoc);
        this.source = source;
        if (source != null)
            projectile_entity.setShooter(source);
        this.projectileCause = projectileCause;

        this.spawnTime = System.currentTimeMillis();
        this.teleported = false;

        //set location and speed
        Location new_loc = projectile_entity.getLocation();
        predictor = new BukkitMovingObject(new_loc, projectile_entity.getVelocity(), projectile.getProjectileEntity());

        this.lastSmokeTrailLocation = CoordinateUtil.fromLoc(new_loc);
    }

    @Override
    public Projectile getProjectileEntity() {
        Entity e = Bukkit.getEntity(entityUID);
        if (e == null)
            return null;

        return (Projectile) Bukkit.getEntity(entityUID);
    }

    /**
     * check if the projectile in in a liquid
     *
     * @return true if the projectile is in a liquid
     */
    public boolean isInWaterCheck(Projectile projectile_entity) {
        if (projectile_entity != null) {
            Block block = projectile_entity.getLocation().getBlock();
            return block.isLiquid();
        }
        return false;
    }

    /**
     * if the projectile is still alive and valid
     * a projectile is valid if it has an entity, is not below -64 and younger than 1h (60*60*1000)
     *
     * @return returns false if the projectile entity is null
     */
    public boolean isValid(Projectile projectile_entity) {
        return (projectile_entity != null && projectile_entity.getLocation().getBlockY() > -64 && System.currentTimeMillis() < getSpawnTime() + 3600000);
    }

    /**
     * returns the calculated location of the projectile
     *
     * @return the location where the projectile should be
     */
    public Location getExpectedLocation() {
        return predictor.getLocation();
    }

    /**
     * returns actual location of the projectile
     *
     * @return momentary position of the projectile
     */
    public Location getActualLocation(Projectile projectile_entity) {
        return projectile_entity.getLocation();
    }

    /**
     * returns the distance of the projectile location to the calculated location
     *
     * @return distance of the projectile location to the calculated location
     */
    public double distanceToProjectile(Projectile projectile_entity) {
        return projectile_entity.getLocation().toVector().distance(VectorUtils.toBaseVector(predictor.getLoc()));
    }

    /**
     * teleports the projectile to the predicted location
     */
    public void teleportToPrediction(Projectile projectile_entity) {
        if (projectile_entity == null)
            return;
        PaperLib.teleportAsync(projectile_entity, predictor.getLocation());
        projectile_entity.setVelocity(VectorUtils.toBaseVector(predictor.getVel()));
    }

    @Override
    public void teleport(Coordinate loc, CannonVector vel) {
        teleport(CoordinateUtil.toLoc(loc), VectorUtils.toBaseVector(vel));
    }

    /**
     * teleports the projectile to the given location
     *
     * @param loc target location
     * @param vel velocity of the projectile
     */
    public void teleport(Location loc, Vector vel) {
        this.predictor.setLocation(loc);
        this.predictor.setVel(VectorUtils.fromBaseVector(vel));
        teleportToPrediction(getProjectileEntity());
    }

    public World getWorld() {
        return Bukkit.getWorld(worldUID);
    }

    public Vector getVelocity() {
        return VectorUtils.toBaseVector(predictor.getVel().clone());
    }
}
