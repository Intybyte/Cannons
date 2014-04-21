package at.pavlov.cannons;

import java.util.*;


import at.pavlov.cannons.event.ProjectileImpactEvent;
import at.pavlov.cannons.event.ProjectilePiercingEvent;
import at.pavlov.cannons.utils.CannonsUtil;
import at.pavlov.cannons.utils.DelayedTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.container.MaterialHolder;
import at.pavlov.cannons.projectile.FlyingProjectile;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.cannons.projectile.ProjectileProperties;

public class CreateExplosion {

    private final Cannons plugin;
    private final Config config;

    private HashSet<UUID> transmittedEntities = new HashSet<UUID>();
    //the entity is used in 1 tick. There should be no garbage collector problem
    private HashMap<Entity,Double> damageMap = new HashMap<Entity, Double>();

    //################### Constructor ############################################
    public CreateExplosion (Cannons plugin, Config config)
    {
        this.plugin = plugin;
        this.config = plugin.getMyConfig();
    }


    /**
     * Breaks a obsidian/water/lava blocks if the projectile has superbreaker
     * @param block
     * @param blocklist
     * @param superBreaker
     * @param blockDamage break blocks if true
     * @return true if the block can be destroyed
     */
    private boolean breakBlock(Block block, List<Block> blocklist, Boolean superBreaker, Boolean blockDamage)
    {
        MaterialHolder destroyedBlock = new MaterialHolder(block.getTypeId(), block.getData());

        //air is not an block to break, so ignore it
        if (!destroyedBlock.equals(Material.AIR))
        {
            //if it is unbreakable, ignore it
            for (MaterialHolder unbreakableBlock : config.getUnbreakableBlocks())
            {
                if (unbreakableBlock.equalsFuzzy(destroyedBlock))
                {
                    //this block is protected and impenetrable
                    return false;
                }
            }

            //test if it needs superbreaker
            for (MaterialHolder superbreakerBlock : config.getSuperbreakerBlocks())
            {
                if ((superbreakerBlock.equalsFuzzy(destroyedBlock)))
                {
                    if (superBreaker)
                    {
                        //this projectile has superbreaker and can destroy this block

                        //don't do damage to blocks if false. But it will penetrate the blocks
                        if (blockDamage)
                            blocklist.add(block);
                        // break it
                        return true;
                    }
                    else
                    {
                        //it has not the superbreaker ability and this block is therefore impenetrable
                        return false;
                    }
                }
            }

            //so it is not protected and not a superbreaker block. So break it
            if (blockDamage)
                blocklist.add(block);
            return true;

        }
        // air can be destroyed
        return true;
    }

    /**
     * breaks blocks that are on the trajectory of the projectile. The projectile is stopped by impenetratable blocks (obsidian)
     * @param cannonball
     * @return
     */
    private Location blockBreaker(FlyingProjectile cannonball)
    {
        Projectile projectile = cannonball.getProjectile();
        org.bukkit.entity.Projectile projectile_entity = cannonball.getProjectileEntity();

        //has this projectile the super breaker property and makes block damage
        Boolean superbreaker = projectile.hasProperty(ProjectileProperties.SUPERBREAKER);
        Boolean doesBlockDamage = projectile.getPenetrationDamage();

        //list of destroy blocks
        LinkedList<Block> blocklist = new LinkedList<Block>();

        Vector vel = projectile_entity.getVelocity();
        Location snowballLoc = projectile_entity.getLocation();
        World world = projectile_entity.getWorld();
        int penetration = (int) ((cannonball.getProjectile().getPenetration()) * vel.length() / projectile.getVelocity());
        Location impactLoc = snowballLoc.clone();

        plugin.logDebug("Projectile impact at: " + impactLoc.getBlockX() + ", "+ impactLoc.getBlockY() + ", " + impactLoc.getBlockZ());
        BlockIterator iter = new BlockIterator(world, snowballLoc.toVector(), vel.normalize(), 0, (int) (vel.length()*2));

        //try to find a surface of the
        while (iter.hasNext())
        {
            Block next = iter.next();
            //if there is no block, go further until we hit the surface
            if (next.isEmpty())
            {
                impactLoc = next.getLocation();
            }
            else
            {
                plugin.logDebug("Found surface at: " + impactLoc.getBlockX() + ", " + impactLoc.getBlockY() + ", " + impactLoc.getBlockZ());
                break;
            }
        }

        // the cannonball will only break blocks if it has penetration.
        if (cannonball.getProjectile().getPenetration() > 0)
        {
            iter = new BlockIterator(world, snowballLoc.toVector(), vel.normalize(), 0, penetration + 1);

            int i=0;
            while (iter.hasNext() && i <= penetration + 1)
            {
                i++;
                Block next = iter.next();
                //Break block on ray
                if (i <= penetration)
                {
                    // if block can be destroyed the the iterator will check the next block. Else the projectile will explode
                    if (!breakBlock(next, blocklist, superbreaker, doesBlockDamage))
                    {
                        //found undestroyable block - set impactloc
                        impactLoc = next.getLocation();
                        break;
                    }
                }
                //set impact location
                else
                {
                    impactLoc = next.getLocation();
                }
            }
        }

        if (superbreaker)
        {
            //small explosion on impact
            Block block = impactLoc.getBlock();
            breakBlock(block, blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.UP), blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.DOWN), blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.SOUTH), blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.WEST), blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.EAST), blocklist, superbreaker, doesBlockDamage);
            breakBlock(block.getRelative(BlockFace.NORTH), blocklist, superbreaker, doesBlockDamage);
        }

        //no eventhandling if the list is empty
        if (blocklist.size() > 0)
        {
            //fire custom piercing event to notify other plugins (blocks can be removed)
            ProjectilePiercingEvent piercingEvent = new ProjectilePiercingEvent(projectile, impactLoc, blocklist);
            plugin.getServer().getPluginManager().callEvent(piercingEvent);

            //create bukkit event
            EntityExplodeEvent event = new EntityExplodeEvent(null, impactLoc, piercingEvent.getBlockList(), 1.0f);
            //handle with bukkit
            plugin.getServer().getPluginManager().callEvent(event);

            plugin.logDebug("explode event canceled: " + event.isCancelled());
            //if not canceled break all given blocks
            if(!event.isCancelled())
            {
                plugin.logDebug("breaking block for penetration event");
                // break water, lava, obsidian if cannon projectile
                for (int i = 0; i < event.blockList().size(); i++)
                {
                    Block pBlock =  event.blockList().get(i);
                    // break the block, no matter what it is
                    BreakBreakNaturally(pBlock,event.getYield());
                }
            }
        }
        return impactLoc;
    }

    /***
     * Breaks a block with a certain yield
     * @param block
     * @param yield
     */
    private void BreakBreakNaturally(Block block, float yield)
    {
        Random r = new Random();
        if (r.nextFloat() > yield)
        {
            block.breakNaturally();
        }
        else
        {
            block.setType(Material.AIR);
        }
    }

    /**
     * places a mob on the given location and pushes it away from the impact
     * @param impactLoc
     * @param loc
     * @param data
     */
    private void PlaceMob(Location impactLoc, Location loc, double entityVelocity, int data, double tntFuse)
    {
        World world = impactLoc.getWorld();
        Random r = new Random();

        Integer mobList[] = {50,51,52,54,55,56,57,58,59,60,61,62,65,66,90,91,92,93,94,95,96,98,120};

        if (data < 0)
        {
            //if all datavalues are allowed create a random spawn
            data = mobList[r.nextInt(mobList.length)];
        }

        Entity entity;
        EntityType entityType = EntityType.fromId(data);
        if (entityType != null)
        {
            //spawn mob
            entity = world.spawnEntity(loc, entityType);
        }
        else
        {
            plugin.logSevere("MonsterEgg ID " + data + " does not exist");
            return;
        }

        if (entity != null)
        {
            //get distance form the center + 1 to avoid division by zero
            double dist = impactLoc.distance(loc) + 1;
            //calculate veloctiy away from the impact
            Vector vect = loc.clone().subtract(impactLoc).toVector().multiply(entityVelocity/dist);
            //set the entity velocity
            entity.setVelocity(vect);
            //for TNT only
            if (entity instanceof TNTPrimed)
            {
                TNTPrimed tnt = (TNTPrimed) entity;
                int fuseTicks = (int)(tntFuse*20.0*(1+r.nextGaussian()/3.0));
                plugin.logDebug("set TNT fuse ticks to: " + fuseTicks);
                tnt.setFuseTicks(fuseTicks);
            }
        }
    }

    /**
     * spawns a falling block with the id and data that is slinged away from the impact
     * @param impactLoc
     * @param loc
     * @param entityVelocity
     * @param item
     */
    private void spawnFallingBlock(Location impactLoc, Location loc, double entityVelocity, MaterialHolder item)
    {
        FallingBlock entity = impactLoc.getWorld().spawnFallingBlock(loc, item.getId(), (byte) item.getData());

        //give the blocks some velocity
        if (entity != null)
        {
            //get distance form the center + 1, to avoid division by zero
            double dist = impactLoc.distance(loc) + 1;
            //calculate veloctiy away from the impact
            Vector vect = loc.clone().subtract(impactLoc).toVector().multiply(entityVelocity/dist);
            //set the entity velocity
            entity.setVelocity(vect);
            //set some other properties
            entity.setDropItem(false);
        }
        else
        {
            plugin.logSevere("Item id:" + item.getId() + " data:" + item.getData() + " can't be spawned as falling block.");
        }
    }

    /**
     * performs the entity placing on the given location
     * @param impactLoc
     * @param loc
     * @param cannonball
     */
    private void makeBlockPlace(Location impactLoc, Location loc, FlyingProjectile cannonball)
    {
        Projectile projectile = cannonball.getProjectile();

        if (canPlaceBlock(loc.getBlock()))
        {
            if (checkLineOfSight(impactLoc, loc) == 0)
            {
                if (projectile == null)
                {
                    plugin.logSevere("no projectile data in flyingprojectile for makeBlockPlace");
                    return;
                }

                for (MaterialHolder placeBlock : projectile.getBlockPlaceList())
                {
                    //check if Material is a mob egg
                    if (placeBlock.equals(Material.MONSTER_EGG))
                    {
                        //else place mob
                        PlaceMob(impactLoc, loc, projectile.getBlockPlaceVelocity(), placeBlock.getData(),projectile.getTntFuseTime());
                    }
                    else
                    {
                        spawnFallingBlock(impactLoc, loc, projectile.getBlockPlaceVelocity(), placeBlock);
                    }
                }
            }
        }
    }

    /**
     * performs the block spawning for the given projectile
     * @param impactLoc
     * @param cannonball
     */
    private void spreadBlocks(FlyingProjectile cannonball, Location impactLoc)
    {
        Projectile projectile = cannonball.getProjectile();

        if (projectile.doesBlockPlace())
        {
            Random r = new Random();
            Location loc;

            double spread = projectile.getBlockPlaceRadius();
            //add some randomness to the amount of spawned blocks
            int maxPlacement = (int) Math.round(projectile.getBlockPlaceAmount() * (1+r.nextGaussian()/3.0));


            //iterate blocks around to get a good spot
            int placedBlocks = 0;
            int iterations1 = 0;
            do
            {
                iterations1 ++;

                loc = impactLoc.clone();
                //get new position
                loc.setX(loc.getX() + r.nextGaussian()*spread/2.0);
                loc.setZ(loc.getZ() + r.nextGaussian()*spread/2.0);

                //check a entity can spawn on this block
                if (canPlaceBlock(loc.getBlock()))
                {
                    placedBlocks++;
                    //place the block
                    makeBlockPlace(impactLoc, loc, cannonball);
                }
            } while (iterations1 < maxPlacement && placedBlocks < maxPlacement);
        }
    }

    /**
     * returns true if an entity can be place on this block
     * @param block
     * @return
     */
    private boolean canPlaceBlock(Block block)
    {
        return block.getType() == Material.AIR || block.getType() == Material.FIRE || block.isLiquid();
    }


    //####################################  checkLineOfSight ##############################
    private int checkLineOfSight(Location impact, Location target)
    {
        int blockingBlocks = 0;

        // vector pointing from impact to target
        Vector vect = target.toVector().clone().subtract(impact.toVector());
        int length = (int) Math.ceil(vect.length());
        vect.normalize();


        Location impactClone = impact.clone();
        for (int i = 2; i <= length; i++)
        {
            // check if line of sight is blocked
            if (impactClone.add(vect).getBlock().getType() != Material.AIR)
            {
                blockingBlocks ++;
            }
        }
        return blockingBlocks;
    }

    /**
     * Gives a player next to an explosion an entity effect
     * @param impactLoc
     * @param next
     * @param cannonball
     */
    private void applyPotionEffect(Location impactLoc, Entity next, FlyingProjectile cannonball)
    {
        Projectile projectile = cannonball.getProjectile();

        if (next instanceof LivingEntity)
        {
            LivingEntity living = (LivingEntity) next;

            double dist = impactLoc.distance(living.getEyeLocation());
            //if the entity is too far away, return
            if (dist > projectile.getPotionRange()) return;

            // duration of the potion effect
            double duration = projectile.getPotionDuration()*20;

            //check line of sight and reduce damage if the way is blocked
            int blockingBlocks = checkLineOfSight(impactLoc, living.getEyeLocation());
            duration = duration / (blockingBlocks + 1);

            //randomizer
            Random r = new Random();
            float rand = r.nextFloat();
            duration *= rand/2 + 0.5;

            // apply potion effect if the duration is not small then 1 tick
            if (duration >= 1)
            {
                int intDuration = (int) Math.floor(duration);

                for (PotionEffectType potionEffect : projectile.getPotionsEffectList())
                {
                    // apply to entity
                    potionEffect.createEffect(intDuration, projectile.getPotionAmplifier()).apply(living);
                }
            }
        }
    }

    /**
     * Returns the amount of damage the livingEntity receives due to explosion of the projectile
     * @param impactLoc
     * @param next
     * @param cannonball
     * @return - damage done to the entity
     */
    private double getPlayerDamage(Location impactLoc, Entity next, FlyingProjectile cannonball)
    {
        Projectile projectile = cannonball.getProjectile();

        if (next instanceof LivingEntity)
        {
            LivingEntity living = (LivingEntity) next;

            double dist = impactLoc.distance((living).getEyeLocation());
            plugin.logDebug("Distance of " + living.getType() + " to impact: " + String.format("%.2f", dist));
            //if the entity is too far away, return
            if (dist > projectile.getPlayerDamageRange()) return 0.0;

            //given damage is in half hearts
            double damage = projectile.getPlayerDamage();

            //check line of sight and reduce damage if the way is blocked
            int blockingBlocks = checkLineOfSight(impactLoc, living.getEyeLocation());
            damage = damage / (blockingBlocks + 1);

            //randomizer
            Random r = new Random();
            float rand = r.nextFloat();
            damage *= (rand + 0.5);

            //calculate the armor reduction
            double reduction = 1.0;
            if (living instanceof Player)
            {
                Player player = (Player) living;
                double armorPiercing = Math.max(projectile.getPenetration(),0);
                reduction *= (1-CannonsUtil.getArmorDamageReduced(player)/(armorPiercing+1)) * (1-CannonsUtil.getBlastProtection(player));
            }

            plugin.logDebug("PlayerDamage " + living.getType() + ": " + String.format("%.2f", damage) + ", reduction: " + String.format("%.2f", reduction));

            damage = damage * reduction;

            return damage;
        }
        //if the entity is not alive
        return 0.0;
    }


    /**
     * Returns the amount of damage dealt to an entity by the projectile
     * @param cannonball
     * @param target
     * @return return the amount of damage done to the living entity
     */
    private double getDirectHitDamage(FlyingProjectile cannonball, Entity target)
    {
        Projectile projectile = cannonball.getProjectile();

        if (cannonball.getProjectileEntity()==null)
            return 0.0;


        if (target instanceof LivingEntity)
        {
            LivingEntity living = (LivingEntity) target;

            //given damage is in half hearts
            double damage = projectile.getDirectHitDamage();

            //randomizer
            Random r = new Random();
            float rand = r.nextFloat();
            damage *= (rand + 0.5);

            //calculate the armor reduction
            double reduction = 1.0;
            if (living instanceof Player)
            {
                Player player = (Player) living;
                double armorPiercing = Math.max(projectile.getPenetration(),0);
                reduction *= (1-CannonsUtil.getArmorDamageReduced(player)/(armorPiercing+1)) * (1-CannonsUtil.getProjectileProtection(player)/(armorPiercing+1));
            }

            plugin.logDebug("DirectHitDamage " + living.getType() + ": " + String.format("%.2f", damage) + ", reduction: " + String.format("%.2f", reduction));
            return damage * reduction;
        }
        //if the entity is not living
        return 0.0;
    }

    /**
     * the given entity was hit by a cannonball
     * @param cannonball cannonball which hit the entity
     * @param entity entity hit
     */
    public void directHit(FlyingProjectile cannonball, Entity entity)
    {
        //add damage to map - it will be applied later to the player
        double directHit = getDirectHitDamage(cannonball, entity);
        damageMap.put(entity, directHit);
        //explode the cannonball
        detonate(cannonball);

    }

    /**
     * detonated the cannonball
     * @param cannonball cannonball which will explode
     */
    public void detonate(FlyingProjectile cannonball)
    {
        plugin.logDebug("detonate cannonball");

        Projectile projectile = cannonball.getProjectile().clone();
        org.bukkit.entity.Projectile projectile_entity = cannonball.getProjectileEntity();

        LivingEntity shooter = cannonball.getShooter();
        Player player = null;
        if (shooter instanceof Player)
            player = (Player) shooter;

        //breaks blocks from the impact of the projectile to the location of the explosion
        Location impactLoc = blockBreaker(cannonball);

        //get world
        World world = impactLoc.getWorld();

        //teleport snowball to impact
        projectile_entity.teleport(impactLoc);

        float explosion_power = projectile.getExplosionPower();

        //reset explosion power if it is underwater and not allowed
        plugin.logDebug("Explosion is underwater: " + cannonball.wasInWater());
        if (!projectile.isUnderwaterDamage() && cannonball.wasInWater())
        {
            plugin.logDebug("Underwater explosion not allowed. Event cancelled");
            return;
        }

        //fire impact event
        ProjectileImpactEvent impactEvent = new ProjectileImpactEvent(projectile, impactLoc);
        Bukkit.getServer().getPluginManager().callEvent(impactEvent);

        //if canceled then exit
        if (impactEvent.isCancelled())
        {
            //event canceled, make some effects - even if the area is protected by a plugin
            world.createExplosion(impactLoc.getX(), impactLoc.getY(), impactLoc.getZ(), 0);
            sendExplosionToPlayers(impactLoc);
            return;
        }

        //explosion event
        boolean incendiary = projectile.hasProperty(ProjectileProperties.INCENDIARY);
        boolean blockDamage = projectile.getExplosionDamage();
        boolean notCanceled = world.createExplosion(impactLoc.getX(), impactLoc.getY(), impactLoc.getZ(), explosion_power, incendiary, blockDamage);

        //send a message about the impact (only if the projectile has enabled this feature)
        if (projectile.isImpactMessage())
            plugin.displayImpactMessage(player, impactLoc, notCanceled);

        // do nothing if the projectile impact was canceled or it is underwater with deactivated
        if (notCanceled)
        {
            //if the player is too far away, there will be a imitated explosion made of fake blocks
            sendExplosionToPlayers(impactLoc);
            //place blocks around the impact like webs, lava, water
            spreadBlocks(cannonball, impactLoc);
            //spawns additional projectiles after the explosion
            spawnProjectiles(cannonball);
            //spawn fireworks
            spawnFireworks(cannonball);
            //do potion effects
            damageEntity(cannonball, impactLoc);
            //teleport the player to the impact or to the start point
            teleportPlayer(cannonball, impactLoc, player);


            //check which entities are affected by the event
            //List<Entity> EntitiesAfterExplosion = projectile_entity.getNearbyEntities(effectRange, effectRange, effectRange);
            //transmittingEntities(EntitiesAfterExplosion, cannonball.getShooter());//place blocks around the impact like webs, lava, water
        }
    }

    /**
     * teleport the player to the impact or to the starting point, depending on the given projectile properties
     * @param cannonball the flying projectile
     * @param impactLoc location to teleport the player
     * @param player the one to teleport
     */
    private void teleportPlayer(FlyingProjectile cannonball, Location impactLoc, Player player)
    {
        Projectile projectile = cannonball.getProjectile();

        Location teleLoc = null;
        //teleport to impact and reset speed - make a soft landing
        if (projectile.hasProperty(ProjectileProperties.TELEPORT) || projectile.hasProperty(ProjectileProperties.HUMAN_CANNONBALL))
        {
            teleLoc = impactLoc.clone();
        }
        //teleport the player back to the location before firing
        else if(projectile.hasProperty(ProjectileProperties.OBSERVER))
        {
            teleLoc = cannonball.getFiringLocation();
        }
        //teleport to this location
        if (teleLoc != null)
        {
            teleLoc.setYaw(player.getLocation().getYaw());
            teleLoc.setPitch(player.getLocation().getPitch());
            player.teleport(teleLoc);
            player.setVelocity(new Vector(0,0,0));
        }
    }

    /**
     * does additional damage effects to player (directHit, explosion and potion effects)
     * @param cannonball the flying projectile
     * @param impactLoc location of the projectile impact
     */
    private void damageEntity(FlyingProjectile cannonball, Location impactLoc)
    {
        Projectile projectile = cannonball.getProjectile();
        Entity projectile_entity = cannonball.getProjectileEntity();

        int effectRange = (int) projectile.getPotionRange()/2;
        List<Entity> entities = projectile_entity.getNearbyEntities(effectRange, effectRange, effectRange);

        //search all entities to damage
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext())
        {
            Entity next = it.next();
            applyPotionEffect(impactLoc, next, cannonball);

            //get previous damage
            double damage = 0.0;
            if (damageMap.containsKey(next))
                damage = damageMap.get(next);

            //add explosion damage
            damage += getPlayerDamage(impactLoc, next, cannonball);
            //apply sum of all damages
            if (damage >= 1 &&  next instanceof LivingEntity)
            {
                LivingEntity living = (LivingEntity) next;
                plugin.logDebug("damage entity " + living.getType() + " by " + String.format("%.2f", damage));
                double health = living.getHealth();
                living.damage(damage);

                //if player wears armor reduce damage if the player has take damage
                if (living instanceof Player && health > living.getHealth())
                    CannonsUtil.reduceArmorDurability((Player) living);
            }
        }
        //remove all entries in damageMap
        damageMap.clear();
    }



    /**
     * spawns Projectiles given in the spawnProjectile property
     * @param cannonball the flying projectile
     */
    private void spawnProjectiles(FlyingProjectile cannonball)
    {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DelayedTask(cannonball)
        {
            public void run(Object object) {
                FlyingProjectile cannonball = (FlyingProjectile) object;

                Projectile projectile = cannonball.getProjectile();
                LivingEntity shooter = cannonball.getShooter();
                Player player = (Player) shooter;
                Location impactLoc = cannonball.getProjectileEntity().getLocation();


                Random r = new Random();

                for (String strProj : projectile.getSpawnProjectiles())
                {
                    Projectile newProjectiles = plugin.getProjectileStorage().getByName(strProj);
                    if (newProjectiles == null)
                    {
                        plugin.logSevere("Can't use spawnProjectile " + strProj + " because Projectile does not exist");
                        continue;
                    }

                    for (int i=0; i<newProjectiles.getNumberOfBullets(); i++)
                    {
                        Vector vect = new Vector(r.nextDouble()-0.5, r.nextDouble()-0.5, r.nextDouble()-0.5);
                        vect = vect.normalize().multiply(newProjectiles.getVelocity());

                        //don't spawn the projectile in the center
                        Location spawnLoc = impactLoc.clone().add(vect.clone().normalize().multiply(3.0));
                        plugin.getProjectileManager().spawnProjectile(newProjectiles, player, spawnLoc, vect);
                    }
                }
            }
        }, 1L);
    }

    /**
     * spawns fireworks after the explosion
     * @param cannonball the flying projectile
     */
    private void spawnFireworks(FlyingProjectile cannonball)
    {
        World world = cannonball.getProjectileEntity().getWorld();
        Projectile projectile = cannonball.getProjectile();

        //a fireworks needs some colors
        if (projectile.getFireworksColors().size() == 0) return;

        //building the fireworks effect
        FireworkEffect.Builder fwb = FireworkEffect.builder().flicker(projectile.isFireworksFlicker()).trail(projectile.isFireworksTrail()).with(projectile.getFireworksType());
        //setting colors
        for (Integer color : projectile.getFireworksColors())
        {
            fwb.withColor(Color.fromRGB(color));
        }
        for (Integer color : projectile.getFireworksFadeColors())
        {
            fwb.withFade(Color.fromRGB(color));
        }


        //apply to rocket
        final Firework fw = (Firework) world.spawnEntity(cannonball.getProjectileEntity().getLocation(), EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.addEffect(fwb.build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);

        //detonate firework after 1tick. This seems to works much better than detonating instantaneously
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new DelayedTask(fw)
        {
            public void run(Object object) {
                Firework fw = (Firework) object;
                //fw.detonate();
            }
        }, 1L);
    }


    /**
     * event for all entities which died in the explosion
     */
    private void transmittingEntities(List<Entity> after, Entity shooter)
    {
        //exit now
        shooter = null;

        //check if there is a shooter, redstone cannons are not counted
        if (shooter == null) return;
        if (!(shooter instanceof Player)) return;

        //return if the list before is empty
        if (after.size() == 0) return;

        //calculate distance form the shooter location to the first monster
        double distance = 0.0;

        //check which entities have been killed
        List<LivingEntity> killedEntities = new LinkedList<LivingEntity>();
        Iterator<Entity> iter = after.iterator();
        while (iter.hasNext())
        {
            Entity entity = iter.next();
            if (entity instanceof LivingEntity)
            {
                // killed by the explosion
                if (entity.isDead())
                {
                    LivingEntity LivEntity = (LivingEntity) entity;
                    //check if the entity has not been transmitted
                    if(!hasBeenTransmitted(LivEntity.getUniqueId()))
                    {
                        //calculate distance form the shooter location to the first monster
                        distance = shooter.getLocation().distance(LivEntity.getLocation());
                        killedEntities.add(LivEntity);
                        transmittedEntities.add(LivEntity.getUniqueId());
                    }

                }

            }
        }

        // list should not be empty
        if (killedEntities.size() > 0)
        {
            try {
                //handler.updateGunnerReputation((Player) shooter, killedEntities, distance);
            } catch (Exception e) {
                plugin.logSevere("Error adding reputation to player");
            }
        }
    }


    //############### hasBeenTransmitted ########################
    private boolean hasBeenTransmitted(UUID id)
    {
        return transmittedEntities.contains(id);
    }

    /**
     * creates a imitated explosion made of blocks which is transmitted to player in a give distance
     * @param loc location of the explosion
     */
    public void sendExplosionToPlayers(Location loc)
    {
        CannonsUtil.imitateSound(loc, Sound.EXPLODE, config.getImitatedSoundMaximumDistance(), 0.5F);

        if (!config.isImitatedExplosionEnabled())
            return;

        double minDist = config.getImitatedBlockMinimumDistance();
        double maxDist = config.getImitatedBlockMaximumDistance();
        int r = config.getImitatedExplosionSphereSize()/2;
        MaterialHolder mat = config.getImitatedExplosionMaterial();
        int delay = (int) config.getImitatedExplosionTime()*20;


        for(Player p : loc.getWorld().getPlayers())
        {
            Location pl = p.getLocation();
            double distance = pl.distance(loc);

            if(distance >= minDist  && distance <= maxDist)
            {
                plugin.getFakeBlockHandler().imitatedSphere(p, loc, r, mat, delay);
            }
        }

    }



}
