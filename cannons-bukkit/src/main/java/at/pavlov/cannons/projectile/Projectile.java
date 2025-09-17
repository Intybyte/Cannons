package at.pavlov.cannons.projectile;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.builders.ParticleBuilder;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SoundHolder;
import at.pavlov.cannons.container.SpawnEntityHolder;
import at.pavlov.internal.Key;
import at.pavlov.internal.container.SpawnMaterialHolder;
import at.pavlov.internal.key.registries.Registries;
import at.pavlov.internal.projectile.ProjectileProperties;
import at.pavlov.internal.projectile.data.ClusterExplosionData;
import at.pavlov.internal.projectile.data.ExplosionData;
import at.pavlov.internal.projectile.definition.ProjectilePhysics;
import com.cryptomorin.xseries.XEntityType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.FireworkEffect;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Projectile implements Cloneable {
    private String projectileID;
    private String projectileName;
    private String description; //unused
    private String itemName; //unused
    private ItemHolder loadingItem;

    //properties of the cannonball
    @Getter
    @Setter
    private Key projectileDefinitionKey;
    public EntityType getProjectileEntity() {
        ProjectilePhysics pp = Registries.PROJECTILE_PHYSICS.of(projectileDefinitionKey);
        if (pp == null) {
            Cannons.getPlugin().getLogger().severe(projectileID + " -> invalid projectile key: " + projectileDefinitionKey.full());
            return EntityType.SNOWBALL;
        }

        return XEntityType.of(pp.getEntityKey().full()).get().get();
    }

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
    @Getter
    @Setter
    private ParticleBuilder smokeTrailParticle;

    //explosion
    @Getter
    @Setter
    private ExplosionData explosionData = new ExplosionData();

    //potion stuff
    private double potionRange;
    private double potionDuration;
    private int potionAmplifier;
    private List<PotionEffectType> potionsEffectList = new ArrayList<>();
    private boolean impactIndicator;

    //cluster
    @Getter
    @Setter
    private ClusterExplosionData clusterExplosionData = new ClusterExplosionData();

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
    private boolean fireworksFlicker;
    private boolean fireworksTrail;
    private FireworkEffect.Type fireworksType;
    private List<Integer> fireworksColors;
    private List<Integer> fireworksFadeColors;

    //messages
    private boolean impactMessage;

    //sounds
    private SoundHolder soundLoading;
    private SoundHolder soundImpact;
    private SoundHolder soundImpactProtected;
    private SoundHolder soundImpactWater;

    //permissions
    private List<String> permissionLoad = new ArrayList<>();

    public Projectile(String id) {
        this.projectileID = id;
    }

    @Override
    public Projectile clone() {
        try {
            // call clone in Object.
            return (Projectile) super.clone();
        } catch (CloneNotSupportedException e) {
            Cannons.logger().info("Cloning not allowed.");
            return this;
        }
    }

    /**
     * returns true if both the id and data are equivalent of data == -1
     * @param materialHolder the material of the loaded item
     * @return true if the materials match
     */
    public boolean equals(ItemHolder materialHolder) {
        return loadingItem.equalsFuzzy(materialHolder);
    }

    /**
     * returns true if both the id and data are equivalent of data == -1
     * @param projectileID the file name id of the projectile
     * @return true if the id matches
     */
    public boolean equals(String projectileID) {
        return this.projectileID.equals(projectileID);
    }


    /**
     * returns ID, Data, name and lore of the projectile loading item
     * @return ID, Data, name and lore of the projectile loading item
     */
    public String toString() {
        return loadingItem.toString();
    }

    /**
     * returns ID and data of the loadingItem
     * @return ID and data of the loadingItem
     */
    public String getMaterialInformation() {
        return loadingItem.getType().toString();
    }

    /**
     * returns true if the projectile has this property
     * @param properties properties of the projectile
     * @return true if the projectile has this property
     */
    public boolean hasProperty(ProjectileProperties properties) {
        for (ProjectileProperties propEnum : this.getPropertyList()) {
            if (propEnum.equals(properties)) return true;
        }
        return false;
    }

    /**
     * returns true if the player has permission to use that projectile
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


    public String getItemName() {
        return itemName;
    }


    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public String getProjectileName() {
        return projectileName;
    }


    public void setProjectileName(String projectileName) {
        this.projectileName = projectileName;
    }


    public double getVelocity() {
        return velocity;
    }


    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }


    public double getPenetration() {
        return penetration;
    }


    public void setPenetration(double penetration) {
        this.penetration = penetration;
    }


    public double getTimefuse() {
        return timefuse;
    }


    public void setTimefuse(double timefuse) {
        this.timefuse = timefuse;
    }


    public int getNumberOfBullets() {
        return numberOfBullets;
    }


    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }


    public double getSpreadMultiplier() {
        return spreadMultiplier;
    }


    public void setSpreadMultiplier(double spreadMultiplier) {
        this.spreadMultiplier = spreadMultiplier;
    }


    public List<ProjectileProperties> getPropertyList() {
        return propertyList;
    }


    public void setPropertyList(List<ProjectileProperties> propertyList) {
        this.propertyList = propertyList;
    }


    public double getPotionRange() {
        return potionRange;
    }


    public void setPotionRange(double potionRange) {
        this.potionRange = potionRange;
    }


    public double getPotionDuration() {
        return potionDuration;
    }


    public void setPotionDuration(double potionDuration) {
        this.potionDuration = potionDuration;
    }


    public int getPotionAmplifier() {
        return potionAmplifier;
    }


    public void setPotionAmplifier(int potionAmplifier) {
        this.potionAmplifier = potionAmplifier;
    }


    public List<PotionEffectType> getPotionsEffectList() {
        return potionsEffectList;
    }


    public void setPotionsEffectList(List<PotionEffectType> potionsEffectList) {
        this.potionsEffectList = potionsEffectList;
    }

    public String getProjectileID() {
        return projectileID;
    }

    public void setProjectileID(String projectileID) {
        this.projectileID = projectileID;
    }

    // typo of getProjectileID
    @Deprecated(forRemoval = true)
    public String getProjectileId() {
        return projectileID;
    }

    public ItemHolder getLoadingItem() {
        return loadingItem;
    }

    public void setLoadingItem(ItemHolder loadingItem) {
        this.loadingItem = loadingItem;
    }


    // typo getter for isExplosionDamage
    @Deprecated(forRemoval = true)
    public boolean getExplosionDamage() {
        return explosionData.isExplosionDamage();
    }

    // typo getter for isPenetrationDamage
    @Deprecated(forRemoval = true)
    public boolean getPenetrationDamage() {
        return explosionData.isPenetrationDamage();
    }

    public List<String> getPermissionLoad() {
        return permissionLoad;
    }

    public void setPermissionLoad(List<String> permissionLoad) {
        this.permissionLoad = permissionLoad;
    }

    public boolean isImpactMessage() {
        return impactMessage;
    }

    public void setImpactMessage(boolean impactMessage) {
        this.impactMessage = impactMessage;
    }

    public boolean isFireworksFlicker() {
        return fireworksFlicker;
    }

    public void setFireworksFlicker(boolean fireworksFlicker) {
        this.fireworksFlicker = fireworksFlicker;
    }

    public FireworkEffect.Type getFireworksType() {
        return fireworksType;
    }

    public void setFireworksType(FireworkEffect.Type fireworksType) {
        this.fireworksType = fireworksType;
    }

    public List<Integer> getFireworksColors() {
        return fireworksColors;
    }

    public void setFireworksColors(List<Integer> fireworksColors) {
        this.fireworksColors = fireworksColors;
    }

    public List<Integer> getFireworksFadeColors() {
        return fireworksFadeColors;
    }

    public void setFireworksFadeColors(List<Integer> fireworksFadeColors) {
        this.fireworksFadeColors = fireworksFadeColors;
    }

    public boolean isFireworksTrail() {
        return fireworksTrail;
    }

    public void setFireworksTrail(boolean fireworksTrail) {
        this.fireworksTrail = fireworksTrail;
    }

    public double getAutomaticFiringDelay() {
        return automaticFiringDelay;
    }

    public void setAutomaticFiringDelay(double automaticFiringDelay) {
        this.automaticFiringDelay = automaticFiringDelay;
    }

    public int getAutomaticFiringMagazineSize() {
        return automaticFiringMagazineSize;
    }

    public void setAutomaticFiringMagazineSize(int automaticFiringMagazineSize) {
        this.automaticFiringMagazineSize = automaticFiringMagazineSize;
    }

    public boolean isFireworksEnabled() {
        return fireworksEnabled;
    }

    public void setFireworksEnabled(boolean fireworksEnabled) {
        this.fireworksEnabled = fireworksEnabled;
    }

    public boolean isProjectileOnFire() {
        return projectileOnFire;
    }

    public void setProjectileOnFire(boolean projectileOnFire) {
        this.projectileOnFire = projectileOnFire;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isSpawnEnabled() {
        return spawnEnabled;
    }

    public void setSpawnEnabled(boolean spawnEnabled) {
        this.spawnEnabled = spawnEnabled;
    }

    public double getSpawnVelocity() {
        return spawnVelocity;
    }

    public void setSpawnVelocity(double spawnVelocity) {
        this.spawnVelocity = spawnVelocity;
    }

    public List<SpawnMaterialHolder> getSpawnBlocks() {
        return spawnBlocks;
    }

    public void setSpawnBlocks(List<SpawnMaterialHolder> spawnBlocks) {
        this.spawnBlocks = spawnBlocks;
    }

    public List<SpawnEntityHolder> getSpawnEntities() {
        return spawnEntities;
    }

    public void setSpawnEntities(List<SpawnEntityHolder> spawnEntities) {
        this.spawnEntities = spawnEntities;
    }

    public List<String> getSpawnProjectiles() {
        return spawnProjectiles;
    }

    public void setSpawnProjectiles(List<String> spawnProjectiles) {
        this.spawnProjectiles = spawnProjectiles;
    }

    public double getSpawnEntityRadius() {
        return spawnEntityRadius;
    }

    public void setSpawnEntityRadius(double spawnEntityRadius) {
        this.spawnEntityRadius = spawnEntityRadius;
    }

    public double getSpawnBlockRadius() {
        return spawnBlockRadius;
    }

    public void setSpawnBlockRadius(double spawnBlockRadius) {
        this.spawnBlockRadius = spawnBlockRadius;
    }

    public SoundHolder getSoundLoading() {
        return soundLoading;
    }

    public void setSoundLoading(SoundHolder soundLoading) {
        this.soundLoading = soundLoading;
    }

    public SoundHolder getSoundImpact() {
        return soundImpact;
    }

    public void setSoundImpact(SoundHolder soundImpact) {
        this.soundImpact = soundImpact;
    }

    public SoundHolder getSoundImpactWater() {
        return soundImpactWater;
    }

    public void setSoundImpactWater(SoundHolder soundImpactWater) {
        this.soundImpactWater = soundImpactWater;
    }

    public SoundHolder getSoundImpactProtected() {
        return soundImpactProtected;
    }

    public void setSoundImpactProtected(SoundHolder soundImpactProtected) {
        this.soundImpactProtected = soundImpactProtected;
    }

    public boolean isImpactIndicator() {
        return impactIndicator;
    }

    public void setImpactIndicator(boolean impactIndicator) {
        this.impactIndicator = impactIndicator;
    }

    public boolean isSmokeTrailEnabled() {
        return smokeTrailEnabled;
    }

    public void setSmokeTrailEnabled(boolean smokeTrailEnabled) {
        this.smokeTrailEnabled = smokeTrailEnabled;
    }

    public BlockData getSmokeTrailMaterial() {
        return smokeTrailMaterial;
    }

    public void setSmokeTrailMaterial(BlockData smokeTrailMaterial) {
        this.smokeTrailMaterial = smokeTrailMaterial;
    }

    public double getSmokeTrailDuration() {
        return smokeTrailDuration;
    }

    public void setSmokeTrailDuration(double smokeTrailDuration) {
        this.smokeTrailDuration = smokeTrailDuration;
    }

    public int getSmokeTrailDistance() {
        return smokeTrailDistance;
    }

    public void setSmokeTrailDistance(int smokeTrailDistance) {
        this.smokeTrailDistance = smokeTrailDistance;
    }

    public boolean isSmokeTrailParticleEnabled() {
        return smokeTrailParticleEnabled;
    }

    public void setSmokeTrailParticleEnabled(boolean smokeTrailParticleEnabled) {
        this.smokeTrailParticleEnabled = smokeTrailParticleEnabled;
    }

    public int getSentryIgnoredBlocks() {
        return sentryIgnoredBlocks;
    }

    public void setSentryIgnoredBlocks(int sentryIgnoredBlocks) {
        this.sentryIgnoredBlocks = sentryIgnoredBlocks;
    }

    @Deprecated
    public double getRandomDelay() {
        return this.clusterExplosionData.getRandomDelay();
    }

    @Deprecated
    public boolean isClusterExplosionsEnabled() {
        return this.clusterExplosionData.isClusterExplosionsEnabled();
    }

    @Deprecated
    public void setClusterExplosionsEnabled(boolean clusterExplosionsEnabled) {
        this.clusterExplosionData.setClusterExplosionsEnabled(clusterExplosionsEnabled);
    }

    @Deprecated
    public boolean isClusterExplosionsInBlocks() {
        return this.clusterExplosionData.isClusterExplosionsInBlocks();
    }

    @Deprecated
    public void setClusterExplosionsInBlocks(boolean clusterExplosionsInBlocks) {
        this.clusterExplosionData.setClusterExplosionsInBlocks(clusterExplosionsInBlocks);
    }

    @Deprecated
    public int getClusterExplosionsAmount() {
        return this.clusterExplosionData.getClusterExplosionsAmount();
    }

    @Deprecated
    public void setClusterExplosionsAmount(int clusterExplosionsAmount) {
        this.clusterExplosionData.setClusterExplosionsAmount(clusterExplosionsAmount);
    }

    @Deprecated
    public double getClusterExplosionsMinDelay() {
        return this.clusterExplosionData.getClusterExplosionsMinDelay();
    }

    @Deprecated
    public void setClusterExplosionsMinDelay(double clusterExplosionsMinDelay) {
        this.clusterExplosionData.setClusterExplosionsMinDelay(clusterExplosionsMinDelay);
    }

    @Deprecated
    public double getClusterExplosionsMaxDelay() {
        return this.clusterExplosionData.getClusterExplosionsMaxDelay();
    }

    @Deprecated
    public void setClusterExplosionsMaxDelay(double clusterExplosionsMaxDelay) {
        this.clusterExplosionData.setClusterExplosionsMaxDelay(clusterExplosionsMaxDelay);
    }

    @Deprecated
    public double getClusterExplosionsRadius() {
        return this.clusterExplosionData.getClusterExplosionsRadius();
    }

    @Deprecated
    public void setClusterExplosionsRadius(double clusterExplosionsRadius) {
        this.clusterExplosionData.setClusterExplosionsRadius(clusterExplosionsRadius);
    }

    @Deprecated
    public double getClusterExplosionsPower() {
        return this.clusterExplosionData.getClusterExplosionsPower();
    }

    @Deprecated
    public void setClusterExplosionsPower(double clusterExplosionsPower) {
        this.clusterExplosionData.setClusterExplosionsPower(clusterExplosionsPower);
    }

    @Deprecated
    public float getExplosionPower() {
        return this.explosionData.getExplosionPower();
    }

    @Deprecated
    public void setExplosionPower(float explosionPower) {
        this.explosionData.setExplosionPower(explosionPower);
    }

    @Deprecated
    public boolean isExplosionPowerDependsOnVelocity() {
        return this.explosionData.isExplosionPowerDependsOnVelocity();
    }

    @Deprecated
    public void setExplosionPowerDependsOnVelocity(boolean explosionPowerDependsOnVelocity) {
        this.explosionData.setExplosionPowerDependsOnVelocity(explosionPowerDependsOnVelocity);
    }

    @Deprecated
    public boolean isExplosionDamage() {
        return this.explosionData.isExplosionDamage();
    }

    @Deprecated
    public void setExplosionDamage(boolean explosionDamage) {
        this.explosionData.setExplosionDamage(explosionDamage);
    }

    @Deprecated
    public boolean isUnderwaterDamage() {
        return this.explosionData.isUnderwaterDamage();
    }

    @Deprecated
    public void setUnderwaterDamage(boolean underwaterDamage) {
        this.explosionData.setUnderwaterDamage(underwaterDamage);
    }

    @Deprecated
    public boolean isPenetrationDamage() {
        return this.explosionData.isPenetrationDamage();
    }

    @Deprecated
    public void setPenetrationDamage(boolean penetrationDamage) {
        this.explosionData.setPenetrationDamage(penetrationDamage);
    }

    @Deprecated
    public double getDirectHitDamage() {
        return this.explosionData.getDirectHitDamage();
    }

    @Deprecated
    public void setDirectHitDamage(double directHitDamage) {
        this.explosionData.setDirectHitDamage(directHitDamage);
    }

    @Deprecated
    public double getPlayerDamageRange() {
        return this.explosionData.getPlayerDamageRange();
    }

    @Deprecated
    public void setPlayerDamageRange(double playerDamageRange) {
        this.explosionData.setPlayerDamageRange(playerDamageRange);
    }

    @Deprecated
    public double getPlayerDamage() {
        return this.explosionData.getPlayerDamage();
    }

    @Deprecated
    public void setPlayerDamage(double playerDamage) {
        this.explosionData.setPlayerDamage(playerDamage);
    }
}
