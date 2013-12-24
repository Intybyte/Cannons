package at.pavlov.cannons.projectile;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;



public class FlyingProjectile
{
	private long spawnTime;
	
	private Snowball snowball;
	private Projectile projectile;
    private Location firingLocation;
	
	public FlyingProjectile(Projectile projectile, Snowball snowball, Location firingLoc)
	{
		this.snowball = snowball;
		this.projectile = projectile;
        this.firingLocation = firingLoc;
		this.spawnTime = System.currentTimeMillis();
	}
	
	public LivingEntity getShooter()
	{
		if (this.snowball != null)
			return this.snowball.getShooter();
		return null;
	}
	public void setShooter(Player shooter)
	{
		this.snowball.setShooter(shooter);
	}
	public Snowball getSnowball()
	{
		return snowball;
	}
	public void setSnowball(Snowball snowball)
	{
		this.snowball = snowball;
	}
	public Projectile getProjectile()
	{
		return projectile;
	}
	public void setProjectile(Projectile projectile)
	{
		this.projectile = projectile;
	}
	public long getSpawnTime()
	{
		return spawnTime;
	}
	public void setSpawnTime(long spawnTime)
	{
		this.spawnTime = spawnTime;
	}

    public Location getFiringLocation() {
        return firingLocation;
    }

    public void setFiringLocation(Location firingLocation) {
        this.firingLocation = firingLocation;
    }
}
