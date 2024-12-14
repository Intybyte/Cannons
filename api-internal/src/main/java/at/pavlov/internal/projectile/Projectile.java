package at.pavlov.internal.projectile;

import at.pavlov.internal.container.ItemHolder;
import at.pavlov.internal.container.SoundHolder;
import at.pavlov.internal.container.SpawnEntityHolder;
import at.pavlov.internal.container.SpawnMaterialHolder;
import at.pavlov.internal.enums.ProjectileProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
abstract public class Projectile<
        Entity,
        Potion,
        Block,
        ParticleBuilder,
        Firework,
        IH extends ItemHolder<?>,
        SMH extends SpawnMaterialHolder<Block>,
        SH extends SoundHolder<?>,
        SEH extends SpawnEntityHolder<Entity, ?>
        > implements Cloneable {
    protected String projectileID;
    protected String projectileName;
    protected String description;
    protected String itemName;
    protected IH loadingItem;
    //list of items or blocks that can represent this this (e.g. redstone dust may for wire when you click a block)
    protected List<IH> alternativeItemList = new ArrayList<>();

    //properties of the cannonball
    protected Entity projectileEntity;
    protected boolean projectileOnFire;
    protected double velocity;
    protected double penetration;
    protected double timefuse;
    protected double automaticFiringDelay;
    protected int automaticFiringMagazineSize;
    protected int numberOfBullets;
    protected double spreadMultiplier;
    protected int sentryIgnoredBlocks;
    protected List<ProjectileProperties> propertyList = new ArrayList<>();

    //smokeTrail
    protected boolean smokeTrailEnabled;
    protected int smokeTrailDistance;
    protected Block smokeTrailMaterial;
    protected double smokeTrailDuration;
    protected boolean smokeTrailParticleEnabled;
    protected ParticleBuilder smokeTrailParticle;

    //explosion
    protected float explosionPower;
    protected boolean explosionPowerDependsOnVelocity;
    protected boolean explosionDamage;
    protected boolean underwaterDamage;
    protected boolean penetrationDamage;
    protected double directHitDamage;
    protected double playerDamageRange;
    protected double playerDamage;
    protected double potionRange;
    protected double potionDuration;
    protected int potionAmplifier;
    protected List<Potion> potionsEffectList = new ArrayList<>();
    protected boolean impactIndicator;

    //cluster
    protected boolean clusterExplosionsEnabled;
    protected boolean clusterExplosionsInBlocks;
    protected int clusterExplosionsAmount;
    protected double clusterExplosionsMinDelay;
    protected double clusterExplosionsMaxDelay;
    protected double clusterExplosionsRadius;
    protected double clusterExplosionsPower;

    //placeBlock
    protected boolean spawnEnabled;
    protected double spawnBlockRadius;
    protected double spawnEntityRadius;
    protected double spawnVelocity;
    protected List<SMH> spawnBlocks = new ArrayList<>();
    protected List<SEH> spawnEntities = new ArrayList<>();
    protected List<String> spawnProjectiles;

    //spawn Fireworks
    protected boolean fireworksEnabled;
    protected Firework fireworkEffect;

    //messages
    protected boolean impactMessage;

    //sounds
    protected SH soundLoading;
    protected SH soundImpact;
    protected SH soundImpactProtected;
    protected SH soundImpactWater;

    //permissions
    protected List<String> permissionLoad = new ArrayList<String>();

    public Projectile(String id) {
        this.projectileID = id;
    }

    /**
     * returns true if both the id and data are equivalent of data == -1
     *
     * @param materialHolder the material of the loaded item
     * @return true if the materials match
     */
    public boolean check(IH materialHolder) {
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
}
