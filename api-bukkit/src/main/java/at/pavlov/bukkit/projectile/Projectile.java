package at.pavlov.bukkit.projectile;

import at.pavlov.bukkit.builders.ParticleBuilder;
import at.pavlov.bukkit.container.BukkitSoundHolder;
import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.bukkit.container.SpawnEntityHolder;
import at.pavlov.bukkit.container.SpawnMaterialHolder;
import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.enums.ProjectileProperties;
import lombok.Data;
import org.bukkit.FireworkEffect;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;


@Data
public class Projectile implements Cloneable {
    private String projectileID;
    private String projectileName;
    private String description;
    private String itemName;
    private BukkitItemHolder loadingItem;
    //list of items or blocks that can represent this this (e.g. redstone dust may for wire when you click a block)
    private List<BukkitItemHolder> alternativeItemList = new ArrayList<>();

    //properties of the cannonball
    private EntityType projectileEntity;
    private boolean projectileOnFire;
    private double velocity;
    private double penetration;
    private double timefuse;
    private double automaticFiringDelay;
    private int automaticFiringMagazineSize;
    private int numberOfBullets;
    private double spreadMultiplier;
    private int sentryIgnoredBlocks;
    private List<ProjectileProperties> propertyList = new ArrayList<>();

    //smokeTrail
    private boolean smokeTrailEnabled;
    private int smokeTrailDistance;
    private BlockData smokeTrailMaterial;
    private double smokeTrailDuration;
    private boolean smokeTrailParticleEnabled;
    private ParticleBuilder smokeTrailParticle;

    //explosion
    private float explosionPower;
    private boolean explosionPowerDependsOnVelocity;
    private boolean explosionDamage;
    private boolean underwaterDamage;
    private boolean penetrationDamage;
    private double directHitDamage;
    private double playerDamageRange;
    private double playerDamage;
    private double potionRange;
    private double potionDuration;
    private int potionAmplifier;
    private List<PotionEffectType> potionsEffectList = new ArrayList<>();
    private boolean impactIndicator;

    //cluster
    private boolean clusterExplosionsEnabled;
    private boolean clusterExplosionsInBlocks;
    private int clusterExplosionsAmount;
    private double clusterExplosionsMinDelay;
    private double clusterExplosionsMaxDelay;
    private double clusterExplosionsRadius;
    private double clusterExplosionsPower;

    //placeBlock
    private boolean spawnEnabled;
    private double spawnBlockRadius;
    private double spawnEntityRadius;
    private double spawnVelocity;
    private List<SpawnMaterialHolder> spawnBlocks = new ArrayList<>();
    private List<SpawnEntityHolder> spawnEntities = new ArrayList<>();
    private List<String> spawnProjectiles;

    //spawn Fireworks
    private boolean fireworksEnabled;
    private FireworkEffect.Builder fireworkEffect;

    //messages
    private boolean impactMessage;

    //sounds
    private BukkitSoundHolder soundLoading;
    private BukkitSoundHolder soundImpact;
    private BukkitSoundHolder soundImpactProtected;
    private BukkitSoundHolder soundImpactWater;

    //permissions
    private List<String> permissionLoad = new ArrayList<String>();

    public Projectile(String id) {
        this.projectileID = id;
    }

    @Override
    public Projectile clone() {
        try {
            // call clone in Object.
            return (Projectile) super.clone();
        } catch (CloneNotSupportedException e) {
            CannonLogger.getLogger().info("Cloning not allowed.");
            return this;
        }
    }

    /**
     * returns true if both the id and data are equivalent of data == -1
     *
     * @param materialHolder the material of the loaded item
     * @return true if the materials match
     */
    public boolean check(BukkitItemHolder materialHolder) {
        return loadingItem.equalsFuzzy(materialHolder);
    }

    /**
     * returns true if both the id and data are equivalent of data == -1
     *
     * @param projectileID the file name id of the projectile
     * @return true if the id matches
     */
    public boolean check(String projectileID) {
        return this.projectileID.equals(projectileID);
    }


    /**
     * returns ID, Data, name and lore of the projectile loading item
     *
     * @return ID, Data, name and lore of the projectile loading item
     */
    public String toString() {
        return loadingItem.toString();
    }

    /**
     * returns ID and data of the loadingItem
     *
     * @return ID and data of the loadingItem
     */
    public String getMaterialInformation() {
        return loadingItem.getType().toString();
    }

    /**
     * returns true if the projectile has this property
     *
     * @param properties properties of the projectile
     * @return true if the projectile has this property
     */
    public boolean hasProperty(ProjectileProperties properties) {
        for (ProjectileProperties propEnum : this.getPropertyList()) {
            if (propEnum.equals(properties))
                return true;
        }
        return false;
    }

    /**
     * returns true if the player has permission to use that projectile
     *
     * @param player who tried to load this projectile
     * @return true if the player can load this projectile
     */
    public boolean hasPermission(Player player) {
        if (player == null) return true;

        for (String perm : permissionLoad) {
            if (!player.hasPermission(perm)) {
                //missing permission
                return false;
            }
        }
        //player has all permissions
        return true;
    }
}
