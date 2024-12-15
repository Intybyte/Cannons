package at.pavlov.bukkit.container;

import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.internal.container.MovingObject;
import at.pavlov.internal.container.location.CannonVector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class BukkitMovingObject extends MovingObject<EntityType> {

    public BukkitMovingObject(UUID world, CannonVector loc, CannonVector vel, EntityType entityType) {
        super(world, loc, vel, entityType);
    }

    public BukkitMovingObject(Location newLoc, Vector velocity, EntityType projectileEntity) {
        super(newLoc.getWorld().getUID(), VectorUtils.fromLoc(newLoc), VectorUtils.fromBaseVector(velocity), projectileEntity);
    }

    /**
     * teleports the projectile to this location
     * @param loc the projectile will be teleported to this location
     * @param vel velocity of the object
     */
    public void teleport(Location loc, Vector vel)
    {
        this.loc = VectorUtils.fromBaseVector(loc.toVector());
        this.vel = VectorUtils.fromBaseVector(vel);
        this.world = loc.getWorld().getUID();
    }

    /**
     * returns the calculated location of the projectile
     * @return the location where the projectile should be
     */
    public Location getLocation()
    {
        return VectorUtils.toLoc(Bukkit.getWorld(world), loc);
    }

    public void setLocation(Location loc)
    {
        this.loc =  VectorUtils.fromBaseVector(loc.toVector());
        this.world = loc.getWorld().getUID();
    }


    @Override
    public EntityType arrow() {
        return EntityType.ARROW;
    }

    static private final List<EntityType> fireballs = List.of(EntityType.FIREBALL, EntityType.SMALL_FIREBALL);
    @Override
    public List<EntityType> fireballs() {
        return fireballs;
    }
}

