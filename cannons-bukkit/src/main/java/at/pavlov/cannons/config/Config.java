package at.pavlov.cannons.config;


import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.builders.ParticleBuilder;
import at.pavlov.cannons.cannon.CannonManager;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.utils.ArmorCalculationUtil;
import at.pavlov.cannons.utils.CannonsUtil;
import at.pavlov.cannons.utils.FileUtils;
import at.pavlov.cannons.utils.ParseUtils;
import com.cryptomorin.xseries.particles.XParticle;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author DerPavlov
 * 
 */

@Data public class Config {
	//general
	private boolean debugMode;
    private boolean relayExplosionEvent;
    private int claimEdgeLength;
    private String localization;

    //region hooks
    private boolean economyEnabled;

    //movecraft
    private boolean movecraftEnabled;
    private boolean movecraftCannonEnabled;
    //endregion
	
	//build limits
    private boolean buildLimitEnabled;
    private int buildLimitA;
    private int buildLimitB;
    //keepProjectileAlive
    private boolean keepAliveEnabled;
    private double keepAliveTeleportDistance;
	//tools
    private ItemHolder toolAdjust = new ItemHolder("minecraft:air");
    private ItemHolder toolAutoaim = new ItemHolder("minecraft:clock");
    private double toolAutoaimRange;
    private ItemHolder toolFiring = new ItemHolder("minecraft:flint_and_steel");
    private ItemHolder toolRamrod = new ItemHolder("minecraft:stick");
    private ItemHolder toolRotating = new ItemHolder("minecraft:rail");
    private ItemHolder toolThermometer = new ItemHolder("minecraft:gold_nugget");

    private int imitatedBlockMinimumDistance;
    private int imitatedBlockMaximumDistance;
    private int imitatedSoundMaximumDistance;
    private float imitatedSoundMaximumVolume;

    private boolean imitatedExplosionEnabled;
    private int imitatedExplosionSphereSize;
    private BlockData imitatedExplosionMaterial= Bukkit.createBlockData("minecraft:glowstone");
    private double imitatedExplosionTime;

    private boolean imitatedExplosionParticlesEnabled;
    private Particle imitatedExplosionParticlesType;
    private double imitatedExplosionParticlesDiameter;
    private int imitatedExplosionParticlesCount;

    private boolean imitatedAimingEnabled;
    private int imitatedAimingLineLength;
    private BlockData imitatedAimingMaterial = Bukkit.createBlockData("minecraft:glass");
    private double imitatedAimingTime;

    private boolean imitatedAimingParticleEnabled;
    private ParticleBuilder imitatedAimingParticle;

    private boolean imitatedFiringEffectEnabled;
    private BlockData imitatedFireMaterial = Bukkit.createBlockData("minecraft:glowstone");
    private BlockData imitatedSmokeMaterial = Bukkit.createBlockData("minecraft:cobweb");
    private double imitatedFiringTime;

    private boolean imitatedPredictorEnabled;
    private int imitatedPredictorIterations;
    private double imitatedPredictorDistance;
    private BlockData imitatedPredictorMaterial = Bukkit.createBlockData("minecraft:glowstone");
    private double imitatedPredictorTime;


    //superbreakerBlocks
    private List<BlockData> superbreakerBlocks = new ArrayList<>();

    //unbreakableBlocks
    private List<BlockData> unbreakableBlocks = new ArrayList<>();

    //cancelEventForLoadingItem
    private List<ItemHolder> cancelItems = new ArrayList<>();

	private final Cannons plugin;
    @Getter
    private static Config instance = null;

	private Config(Cannons plugin) {
		this.plugin = plugin;
        this.loadConfig();
	}

    public static void initialize(Cannons plugin) {
        if (instance != null) {
            return;
        }

        instance = new Config(plugin);
    }

	public void loadConfig()  {
		// copy the default config to the disk if it does not exist
		plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration config = plugin.getConfig();
		
		//general
		setDebugMode(config.getBoolean("general.debugMode", false));

        //hooks
        setEconomyEnabled(config.getBoolean("hooks.vault.enabled", true));
        setMovecraftEnabled(config.getBoolean("hooks.movecraft.enabled", true));
        setMovecraftCannonEnabled(config.getBoolean("hooks.movecraftCombat.enabled", true));

        setRelayExplosionEvent(config.getBoolean("general.relayExplosionEvent", false));
        setClaimEdgeLength(config.getInt("general.claimEdgeLength", 60));
        setLocalization(config.getString("general.localization", "localization"));

		//limitOfCannons
		setBuildLimitEnabled(config.getBoolean("cannonLimits.useLimits", true));
		setBuildLimitA(config.getInt("cannonLimits.buildLimitA", 10));
		setBuildLimitB(config.getInt("cannonLimits.buildLimitB", 2));

        //keepProjectileAlive
        setKeepAliveEnabled(config.getBoolean("keepProjectileAlive.enabled", true));
        setKeepAliveTeleportDistance(config.getDouble("keepProjectileAlive.teleportProjectile", 5.0));

		//tools
		setToolAdjust(new ItemHolder(config.getString("tools.adjust", "minecraft:air")));
		setToolAutoaim(new ItemHolder(config.getString("tools.autoaim", "minecraft:clock")));
        setToolAutoaimRange(config.getDouble("tools.autoaimRange", 4.0));
		setToolFiring(new ItemHolder(config.getString("tools.firing", "minecraft:flint_and_steel")));
        setToolRamrod(new ItemHolder(config.getString("tools.ramrod", "minecraft:stick")));
		setToolRotating(new ItemHolder(config.getString("tools.adjust", "minecraft:rail")));
        setToolThermometer(new ItemHolder(config.getString("tools.thermometer", "minecraft:gold_nugget")));

        //imitated effects
        setImitatedBlockMinimumDistance(config.getInt("imitatedEffects.minimumBlockDistance", 40));
        setImitatedBlockMaximumDistance(config.getInt("imitatedEffects.maximumBlockDistance", 200));
        setImitatedSoundMaximumDistance(config.getInt("imitatedEffects.maximumSoundDistance", 200));
        setImitatedSoundMaximumVolume((float) config.getDouble("imitatedEffects.maximumSoundVolume", 0.8));

        //imitated explosions block
        setImitatedExplosionEnabled(config.getBoolean("imitatedEffects.explosion.enabled", false));
        setImitatedExplosionSphereSize(config.getInt("imitatedEffects.explosion.sphereSize", 2));
        setImitatedExplosionMaterial(CannonsUtil.createBlockData(config.getString("imitatedEffects.explosion.material", "minecraft:glowstone")));
        setImitatedExplosionTime(config.getDouble("imitatedEffects.explosion.time", 1.0));

        //imitated explosions particles
        setImitatedExplosionParticlesEnabled(config.getBoolean("imitatedEffects.explosionParticles.enabled", true));
        try {
            setImitatedExplosionParticlesType(Particle.valueOf(config.getString("imitatedEffects.explosionParticles.type", "EXPLOSION_LARGE")));
        }
        catch(Exception e){
            plugin.logSevere("Type for Explosion particle  is not correct. Please check spelling of " + config.getString("imitatedEffects.explosionParticles.type"));
            setImitatedExplosionParticlesType(XParticle.EXPLOSION.get());
        }
        setImitatedExplosionParticlesCount(config.getInt("imitatedEffects.explosionParticles.count", 5));
        setImitatedExplosionParticlesDiameter(config.getDouble("imitatedEffects.explosionParticles.diameter", 1));

        //imitated aiming
        setImitatedAimingEnabled(config.getBoolean("imitatedEffects.aiming.enabled", false));
        setImitatedAimingLineLength(config.getInt("imitatedEffects.aiming.length", 5));
        setImitatedAimingMaterial(CannonsUtil.createBlockData(config.getString("imitatedEffects.aiming.block", "minecraft:glass")));
        setImitatedAimingTime(config.getDouble("imitatedEffects.aiming.time", 1.0));

        setImitatedAimingParticleEnabled(config.getBoolean("imitatedEffects.aiming.particles.enabled", false));
        setImitatedAimingParticle(FileUtils.readParticleBuilder(config, "imitatedEffects.aiming.particles"));

        //imitated firing effects
        setImitatedFiringEffectEnabled(config.getBoolean("imitatedEffects.firing.enabled", false));
        setImitatedFireMaterial(CannonsUtil.createBlockData(config.getString("imitatedEffects.firing.fireBlock", "minecraft:glowstone")));
        setImitatedSmokeMaterial(CannonsUtil.createBlockData(config.getString("imitatedEffects.firing.smokeBlock", "'minecraft:cobweb")));
        setImitatedFiringTime(config.getDouble("imitatedEffects.firing.time", 1.0));

        //imitaded predictor
        setImitatedPredictorEnabled(config.getBoolean("imitatedEffects.predictor.enabled", true));
        setImitatedPredictorIterations(config.getInt("imitatedEffects.predictor.maxIterations", 500));
        setImitatedPredictorDistance(config.getDouble("imitatedEffects.predictor.maxDistance", 400.0));
        setImitatedPredictorMaterial(CannonsUtil.createBlockData(config.getString("imitatedEffects.predictor.material", "minecraft:glowstone")));
        setImitatedPredictorTime(config.getDouble("imitatedEffects.predictor.time", 1.0));

        //superbreakerBlocks
        setSuperbreakerBlocks(ParseUtils.toBlockDataList(config.getStringList("superbreakerBlocks")));
        //if this list is empty add some blocks
        if (superbreakerBlocks.isEmpty()) {
            plugin.logInfo("superbreakerBlock list is empty");
        }

        //unbreakableBlocks
        setUnbreakableBlocks(ParseUtils.toBlockDataList(config.getStringList("unbreakableBlocks")));
        if (unbreakableBlocks.isEmpty()) {
            plugin.logInfo("unbreakableBlocks list is empty");
        }

        //cancelEventForLoadingItem
        setCancelItems(ParseUtils.toItemHolderList(config.getStringList("cancelEventForLoadingItem")));
	
		//load other configs

        plugin.setDebugMode(debugMode);
	}

    @Deprecated(forRemoval = true)
    public UserMessages getUserMessages() {
        return UserMessages.getInstance();
    }

    @Deprecated(forRemoval = true)
    public CannonManager getCannonManager() {
        return CannonManager.getInstance();
    }

    public boolean isCancelItem(ItemStack item) {
        for (ItemHolder item2 : getCancelItems()) {
            if (item2.equalsFuzzy(item))
                return true;
        }
        return false;
    }
}
