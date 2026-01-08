package at.pavlov.cannons.projectile;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.CreateExplosion;
import at.pavlov.cannons.Enum.ProjectileCause;
import at.pavlov.cannons.dao.AsyncTaskManager;
import at.pavlov.internal.Key;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.definition.CustomProjectileDefinition;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;
import com.google.common.base.Preconditions;
import org.bukkit.*;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectileManager {
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
     *
     * @param plugin - Cannons instance
     */
    private ProjectileManager(Cannons plugin) {
        this.plugin = plugin;
    }

    public Entity spawnProjectile(Projectile projectile, @NotNull UUID shooter, org.bukkit.projectiles.ProjectileSource source, Location playerLoc, Location spawnLoc, Vector velocity, UUID cannonId, ProjectileCause projectileCause) {
        Preconditions.checkNotNull(shooter, "shooter for the projectile can't be null");
        World world = spawnLoc.getWorld();

        //set yaw, pitch for fireballs
        double v = velocity.length();
        spawnLoc.setPitch((float) (Math.acos(velocity.getY() / v) * 180.0 / Math.PI - 90));
        spawnLoc.setYaw((float) (Math.atan2(velocity.getZ(), velocity.getX()) * 180.0 / Math.PI - 90));

        Entity projectileEntity = spawnProjectile(projectile, spawnLoc, velocity, world);

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

    private static final NamespacedKey MOB_TYPE_KEY = new NamespacedKey(Cannons.getPlugin(), "mob_type");
    private @NotNull Entity spawnProjectile(Projectile projectile, Location spawnLoc, Vector velocity, World world) {
        Entity entity = world.spawnEntity(spawnLoc, projectile.getProjectileEntity());

        //calculate firing vector
        entity.setVelocity(velocity);

        Key customEntityKey = projectile.getProjectileDefinitionKey();
        CustomProjectileDefinition definition = Registries.CUSTOM_PROJECTILE_DEFINITION.of(customEntityKey);
        if (definition == null) {
            return entity;
        }

        // allow people to use custom textures for entities
        entity.getPersistentDataContainer().set(MOB_TYPE_KEY, PersistentDataType.STRING, customEntityKey.full());

        entity.setVisualFire(definition.isOnFire());
        entity.setGlowing(definition.isGlowing());

        ProjectilePhysics defaultCase = Registries.DEFAULT_PROJECTILE_DEFINITION_REGISTRY.of(definition.getEntityKey());
        if (defaultCase == null) {
            defaultCase = ProjectilePhysics.DEFAULT;
        }

        if (!defaultCase.matches(definition)) {
            entity.setGravity(false);
        }

        if (entity instanceof Attributable attributable) {
            var uuid = UUID.fromString("cannon:attribute");
            for (var entry : definition.getAttributes().entrySet()) {
                String attrKey = entry.getKey();
                Attribute attribute = Registry.ATTRIBUTE.get(NamespacedKey.minecraft(attrKey));

                if (attribute == null) {
                    Cannons.getPlugin().logSevere("Attribute [" + attrKey + "] doesn't exist");
                    continue;
                }

                var instance = attributable.getAttribute(attribute);
                if (instance == null) {
                    Cannons.getPlugin().logSevere("Attribute [" + attrKey + "] not found for entity " + entity.getType());
                    continue;
                }

                // todo: this AttributeModifier constructor is marked for removal, might want to replace with a more robust implementation
                instance.addModifier(
                        new AttributeModifier(
                                uuid, "attribute_definition", entry.getValue(), AttributeModifier.Operation.ADD_NUMBER
                        )
                );
            }
        }

        if (entity instanceof WitherSkull witherSkull) {
            witherSkull.setCharged(definition.isCharged());
        } else if (entity instanceof AbstractArrow arrow) {
            arrow.setCritical(definition.isCritical());
        } else if (entity instanceof ThrowableProjectile throwable) {
            Material material = Material.matchMaterial(definition.getMaterial().full());
            if (material == null) {
                plugin.logSevere("In custom projectile: " + definition.getKey().full() + " the material key is invalid.");
                material = Material.SNOWBALL;
            }

            ItemStack stack = new ItemStack(material);

            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(definition.getCustomModelData());
            stack.setItemMeta(meta);

            throwable.setItem(stack);
        } else if (entity instanceof ItemDisplay itemDisplay) {
            Material material = Material.matchMaterial(definition.getMaterial().full());
            if (material == null) {
                plugin.logSevere("In custom projectile: " + definition.getKey().full() + " the material key is invalid.");
                material = Material.SNOWBALL;
            }

            ItemStack stack = new ItemStack(material);

            ItemMeta meta = stack.getItemMeta();
            meta.setCustomModelData(definition.getCustomModelData());
            stack.setItemMeta(meta);

            itemDisplay.setItemStack(stack);
        }

        return entity;
    }


    /**
     * detonate a timefused projectile mid air
     *
     * @param cannonball - the cannonball to detonate
     */
    private void detonateTimefuse(final FlyingProjectile cannonball) {
        if (cannonball.getProjectile().getTimefuse() <= 0) {
            return;
        }

        //Delayed Task
        AsyncTaskManager.get().scheduler.runTaskLater(() -> {
            //find given UID in list
            FlyingProjectile fproj = flyingProjectilesMap.get(cannonball.getUID());

            if (fproj == null) {
                return;
            }
            //detonate timefuse
            var projectile_entity = fproj.getProjectileEntity();
            //the projectile might be null
            if (projectile_entity != null) {
                CreateExplosion.getInstance().detonate(cannonball, projectile_entity);
                projectile_entity.remove();
            }
            flyingProjectilesMap.remove(cannonball.getUID());
        }, (long) (cannonball.getProjectile().getTimefuse() * 20));
    }


    /**
     * detonates the given projectile entity
     *
     * @param projectile - the projectile with this entity
     */
    public void detonateProjectile(Entity projectile) {
        FlyingProjectile fproj = flyingProjectilesMap.get(projectile.getUniqueId());
        if (fproj != null) {
            CreateExplosion.getInstance().detonate(fproj, projectile);
            projectile.remove();
            flyingProjectilesMap.remove(fproj.getUID());
        }
    }

    /**
     * detonates the given projectile entity
     *
     * @param cannonball - the projectile with this entity
     * @param target     the entity hit by the projectile
     */
    public void directHitProjectile(Entity cannonball, Entity target) {
        if (cannonball == null || target == null) return;

        FlyingProjectile fproj = flyingProjectilesMap.get(cannonball.getUniqueId());
        if (fproj == null) {
            return;
        }

        Entity projectile_entity = fproj.getProjectileEntity();
        if (!fproj.hasDetonated() && cannonball.isValid()) {
            fproj.setHasDetonated(true);
            CreateExplosion.getInstance().directHit(fproj, projectile_entity, target);
            projectile_entity.remove();
        }

        flyingProjectilesMap.remove(fproj.getUID());
    }

    /**
     * returns true if the given entity is a cannonball projectile
     *
     * @param projectile flying projectile
     * @return true if cannonball projectile
     */
    public boolean isFlyingProjectile(Entity projectile) {
        FlyingProjectile fproj = flyingProjectilesMap.get(projectile.getUniqueId());
        return fproj != null;
    }


    /**
     * returns the list of all flying projectiles
     *
     * @return - the list of all flying projectiles
     */
    public ConcurrentHashMap<UUID, FlyingProjectile> getFlyingProjectiles() {
        return flyingProjectilesMap;
    }

    /**
     * returns the projectile of which the player is passenger
     * if the player is attached to a projectile he will follow its movement
     *
     * @param player is the passenger
     * @return the projectile or null
     */
    public FlyingProjectile getAttachedProjectile(Player player) {
        if (player == null) {
            return null;
        }

        for (FlyingProjectile proj : flyingProjectilesMap.values())
            if (proj.getShooterUID().equals(player.getUniqueId()))
                return proj;
        return null;
    }
}
