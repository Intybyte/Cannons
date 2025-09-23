package at.pavlov.cannons.cannon;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.config.Config;
import at.pavlov.cannons.schematic.formats.WorldEditFormat;
import at.pavlov.cannons.schematic.world.SchematicWorldProcessorImpl;
import at.pavlov.internal.container.DesignFileName;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SimpleBlock;
import at.pavlov.cannons.container.SoundHolder;
import at.pavlov.cannons.exchange.BExchanger;
import at.pavlov.cannons.exchange.ExchangeLoader;
import at.pavlov.cannons.utils.CannonsUtil;
import at.pavlov.cannons.utils.DesignComparator;
import at.pavlov.cannons.utils.ParseUtils;
import at.pavlov.internal.Exchanger;
import at.pavlov.internal.Key;
import lombok.Getter;
import me.vaan.schematiclib.base.block.BlockKey;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.block.ICoord;
import me.vaan.schematiclib.base.formats.SchematicLoader;
import me.vaan.schematiclib.base.schematic.Schematic;
import me.vaan.schematiclib.file.block.FileBlock;
import me.vaan.schematiclib.file.block.FileCoord;
import me.vaan.schematiclib.file.formats.VaanFormat;
import me.vaan.schematiclib.file.schematic.FileSchematic;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class DesignStorage {

	@Getter
    private static DesignStorage instance = null;

	@Getter
    private final List<CannonDesign> cannonDesignList;
	private final Cannons plugin;
	@Getter
    private final Set<Material> cannonBlockMaterials;

	public static void initialize(Cannons cannons) {
		if (instance != null)
			return;

		instance = new DesignStorage(cannons);
		instance.loadCannonDesigns();
	}

    private DesignStorage(Cannons cannons)  {
		plugin = cannons;
		cannonDesignList = new ArrayList<>();
		cannonBlockMaterials = EnumSet.noneOf(Material.class);
	}

	/**
	 * returns a list of all cannon design names
	 * @return list of all cannon design names
	 */
	public ArrayList<String> getDesignIds(){
		ArrayList<String> list = new ArrayList<>();
		for (CannonDesign design : cannonDesignList){
			list.add(design.getDesignID());
		}
		return list;
	}

	/**
	 * loads all custom cannon desgins
	 */
	public void loadCannonDesigns()
	{
		plugin.logInfo("Loading cannon designs");

		//clear designList before loading
		cannonDesignList.clear();
		
		// check if design folder is empty or does not exist
		if (CannonsUtil.isFolderEmpty(getPath()))
		{
			// the folder is empty, copy defaults
			plugin.logInfo("No cannon designs loaded - loading default designs");
			copyDefaultDesigns();
		}

		ArrayList<DesignFileName> designFileList = getDesignFiles();

		// stop if there are no files found
		if (designFileList == null || designFileList.isEmpty())
			return;

		for (DesignFileName designFile : designFileList) {
			plugin.logDebug("loading cannon " + designFile.ymlString());
			CannonDesign cannonDesign = new CannonDesign();
			//load .yml
			loadDesignYml(cannonDesign, designFile.ymlString());
			//load .shematic and add to list if valid
			if (loadDesignSchematic(cannonDesign, designFile.schematicString()))
				cannonDesignList.add(cannonDesign);
		}
		
		//sort the list so the designs with more cannon blocks comes first
		//important if there is a design with one block less but else identically 
		Comparator<CannonDesign> comparator = new DesignComparator();
		cannonDesignList.sort(comparator);

        SchematicWorldProcessorImpl processor = SchematicWorldProcessorImpl.getProcessor();
		for (CannonDesign cannonDesign : cannonDesignList) {
            Schematic schematic = cannonDesign.getSchematicMap().get(BlockFace.NORTH);
            Schematic materials = processor.parseToMaterial(schematic);
            materials.positions().stream()
                .map(IBlock::key)
                .map(key -> NamespacedKey.minecraft(key.key()))
                .map(Registry.MATERIAL::get)
                .filter(mat -> mat != Material.AIR)
                .forEach(cannonBlockMaterials::add);
		}


		for (CannonDesign design : cannonDesignList)
		{
			plugin.logDebug("design " + design.toString());
		}

	}

	/**
	 * returns a list with valid cannon designs (.yml + .schematic)
	 * 
	 * @return
	 */
	private ArrayList<DesignFileName> getDesignFiles() {
		ArrayList<DesignFileName> designList = new ArrayList<>();

		try {
			// check plugin/cannons/designs for .yml and .schematic files
			String ymlFile;
			File folder = new File(getPath());

			File[] listOfFiles = folder.listFiles();
            if (listOfFiles == null) {
                plugin.logSevere("Design folder empty");
                return designList;
            }


			for (File listOfFile : listOfFiles) {
                if (!listOfFile.isFile()) {
                	continue;
				}

                ymlFile = listOfFile.getName();
                if (!ymlFile.endsWith(".yml") && !ymlFile.endsWith(".yaml")) {
                	continue;
				}

                String schematicFile = CannonsUtil.changeExtension(ymlFile, ".schematic");
                String schemFile = CannonsUtil.changeExtension(ymlFile, ".schem");
                String vschemFile = CannonsUtil.changeExtension(ymlFile, ".vschem");

                String[] toCheck = new String[] {schemFile, schematicFile, vschemFile};
                boolean success = false;
                for (String entry: toCheck) {
                    File file = new File(getPath() + entry);
                    if (file.isFile()) {
                        designList.add(new DesignFileName(ymlFile, entry));
                        success = true;
                        break;
                    }
                }

                if (!success) {
                    plugin.logSevere("Schematic is missing for configuration: " + ymlFile);
                }
            }
		} catch (Exception e) {
			plugin.logSevere("Error while checking yml and schematic " + e);
		}
		return designList;
	}

	/**
	 * loads the config for one cannon from the .yml file
     * @param cannonDesign design of the cannon
	 * @param ymlFile of the cannon config file
	 */
	private void loadDesignYml(CannonDesign cannonDesign, String ymlFile)
	{
		// load .yml file
		File cannonDesignFile = new File(getPath() + ymlFile);
		FileConfiguration cannonDesignConfig = YamlConfiguration.loadConfiguration(cannonDesignFile);

		// load all entries of the config file

		// general
		cannonDesign.setDesignID(CannonsUtil.removeExtension(ymlFile));
        cannonDesign.setDesignName(cannonDesignConfig.getString("general.designName", "no cannonName"));
        cannonDesign.setMessageName(cannonDesignConfig.getString("general.messageName", "no messageName"));
        cannonDesign.setDescription(cannonDesignConfig.getString("general.description", "no description for this cannon"));
        cannonDesign.setLastUserBecomesOwner(cannonDesignConfig.getBoolean("general.lastUserBecomesOwner", false));

		// sign
		cannonDesign.setSignRequired(cannonDesignConfig.getBoolean("signs.isSignRequired", false));

		// ammunition
		cannonDesign.setGunpowderName(cannonDesignConfig.getString("ammunition.gunpowderName", "gunpowder"));
		cannonDesign.setGunpowderType(new ItemHolder(cannonDesignConfig.getString("ammunition.gunpowderType", "SULPHUR:0")));
        cannonDesign.setNeedsGunpowder(cannonDesignConfig.getBoolean("ammunition.needsGunpowder", true));
        cannonDesign.setGunpowderConsumption(cannonDesignConfig.getBoolean("ammunition.gunpowderConsumption", true));
        cannonDesign.setProjectileConsumption(cannonDesignConfig.getBoolean("ammunition.projectileConsumption", true));
		cannonDesign.setAmmoInfiniteForPlayer(cannonDesignConfig.getBoolean("ammunition.ammoInfiniteForPlayer", false));
		cannonDesign.setAmmoInfiniteForRedstone(cannonDesignConfig.getBoolean("ammunition.ammoInfiniteForRedstone", false));
		cannonDesign.setAutoreloadRedstone(cannonDesignConfig.getBoolean("ammunition.autoreloadRedstone", false));
		cannonDesign.setRemoveChargeAfterFiring(cannonDesignConfig.getBoolean("ammunition.removeChargeAfterFiring", true));
		cannonDesign.setAutoloadChargeWhenLoadingProjectile(cannonDesignConfig.getBoolean("ammunition.autoloadChargeWhenLoadingProjectile", false));
		cannonDesign.setPreloaded(cannonDesignConfig.getBoolean("ammunition.preloaded", false));

		// barrelProperties
		cannonDesign.setMaxLoadableGunpowder(cannonDesignConfig.getInt("barrelProperties.maxLoadableGunpowder", 1));
		if (cannonDesign.getMaxLoadableGunpowderNormal() <= 0)
			cannonDesign.setMaxLoadableGunpowder(1);
		cannonDesign.setMultiplierVelocity(cannonDesignConfig.getDouble("barrelProperties.multiplierVelocity", 1.0));
		cannonDesign.setSpreadOfCannon(cannonDesignConfig.getDouble("barrelProperties.spreadOfCannon", 5.0));

		// timings
		cannonDesign.setBlastConfusion(cannonDesignConfig.getDouble("timings.blastConfusion", 5.0));
		cannonDesign.setFuseBurnTime(cannonDesignConfig.getDouble("timings.fuseBurnTime", 1.0));
		cannonDesign.setFuseBurnTimeRandomness(cannonDesignConfig.getDouble("timings.fuseBurnTimeRandomness", 0.0));
		cannonDesign.setBarrelCooldownTime(cannonDesignConfig.getDouble("timings.barrelCooldownTime", 1.0));
		cannonDesign.setLoadTime(cannonDesignConfig.getDouble("timings.loadTime", 3.0));

		// angles
		cannonDesign.setDefaultHorizontalFacing(BlockFace.valueOf(cannonDesignConfig.getString("angles.defaultHorizontalFacing", "NORTH").toUpperCase()));
		cannonDesign.setDefaultVerticalAngle(cannonDesignConfig.getDouble("angles.defaultVerticalAngle", 0.0));
		cannonDesign.setMaxHorizontalAngleNormal(cannonDesignConfig.getDouble("angles.maxHorizontalAngle", 45.0));
		cannonDesign.setMinHorizontalAngleNormal(cannonDesignConfig.getDouble("angles.minHorizontalAngle", -45.0));
		cannonDesign.setMaxVerticalAngleNormal(cannonDesignConfig.getDouble("angles.maxVerticalAngle", 45.0));
		cannonDesign.setMinVerticalAngleNormal(cannonDesignConfig.getDouble("angles.minVerticalAngle", -45.0));
        cannonDesign.setMaxHorizontalAngleOnShip(cannonDesignConfig.getDouble("angles.maxHorizontalAngleOnShip", 20.0));
        cannonDesign.setMinHorizontalAngleOnShip(cannonDesignConfig.getDouble("angles.minHorizontalAngleOnShip", -20.0));
        cannonDesign.setMaxVerticalAngleOnShip(cannonDesignConfig.getDouble("angles.maxVerticalAngleOnShip", 30.0));
        cannonDesign.setMinVerticalAngleOnShip(cannonDesignConfig.getDouble("angles.minVerticalAngleOnShip", -30.0));
		cannonDesign.setAngleStepSize(cannonDesignConfig.getDouble("angles.angleStepSize", 0.1));
		cannonDesign.setAngleLargeStepSize(cannonDesignConfig.getDouble("angles.largeStepSize", 1.0));
		cannonDesign.setAngleUpdateSpeed((int) (cannonDesignConfig.getDouble("angles.angleUpdateSpeed", 1.0) * 1000.0));
        cannonDesign.setAngleUpdateMessage(cannonDesignConfig.getBoolean("angles.angleUpdateMessage", false));

        //impactPredictor
        cannonDesign.setPredictorEnabled(cannonDesignConfig.getBoolean("impactPredictor.enabled", true));
        cannonDesign.setPredictorDelay((int) (cannonDesignConfig.getDouble("impactPredictor.delay", 1.0) * 1000.0));
        cannonDesign.setPredictorUpdate((int) (cannonDesignConfig.getDouble("impactPredictor.update", 0.1) * 1000.0));

		//sentry
		cannonDesign.setSentry(cannonDesignConfig.getBoolean("sentry.isSentry", false));
		cannonDesign.setSentryIndirectFire(cannonDesignConfig.getBoolean("sentry.indirectFire", false));
        cannonDesign.setSentryMinRange(cannonDesignConfig.getInt("sentry.minRange", 5));
        cannonDesign.setSentryMaxRange(cannonDesignConfig.getInt("sentry.maxRange", 40));
		cannonDesign.setSentrySpread(cannonDesignConfig.getDouble("sentry.spread", 0.5));
        cannonDesign.setSentryUpdateTime((int) (cannonDesignConfig.getDouble("sentry.update", 1.0) * 1000.0));
        cannonDesign.setSentrySwapTime((int) (cannonDesignConfig.getDouble("sentry.swapTime", 10.0)*1000.0));

        //linkCannons
		cannonDesign.setLinkCannonsEnabled(cannonDesignConfig.getBoolean("linkCannons.enabled", false));
		cannonDesign.setLinkCannonsDistance(cannonDesignConfig.getInt("linkCannons.distance", 0));

        //heatManagement
        cannonDesign.setHeatManagementEnabled(cannonDesignConfig.getBoolean("heatManagement.enabled", false));
        cannonDesign.setAutomaticTemperatureControl(cannonDesignConfig.getBoolean("heatManagement.automaticTemperatureControl", false));
        cannonDesign.setBurnDamage(cannonDesignConfig.getDouble("heatManagement.burnDamage", 0.5));
        cannonDesign.setBurnSlowing(cannonDesignConfig.getDouble("heatManagement.burnSlowing", 5.0));
        cannonDesign.setHeatIncreasePerGunpowder(cannonDesignConfig.getDouble("heatManagement.heatIncreasePerGunpowder", 10.0));
        cannonDesign.setCoolingCoefficient(cannonDesignConfig.getDouble("heatManagement.coolingTimeCoefficient", 160.0));
        cannonDesign.setCoolingAmount(cannonDesignConfig.getDouble("heatManagement.coolingAmount", 50.0));
        cannonDesign.setAutomaticCooling(cannonDesignConfig.getBoolean("heatManagement.automaticCooling", false));
        cannonDesign.setWarningTemperature(cannonDesignConfig.getDouble("heatManagement.warningTemperature", 100.0));
        cannonDesign.setCriticalTemperature(cannonDesignConfig.getDouble("heatManagement.criticalTemperature", 150.0));
        cannonDesign.setMaximumTemperature(cannonDesignConfig.getDouble("heatManagement.maximumTemperature", 200.0));
        cannonDesign.setItemCooling(ParseUtils.toItemHolderList(cannonDesignConfig.getStringList("heatManagement.coolingItems")));
        cannonDesign.setItemCoolingUsed(ParseUtils.toItemHolderList(cannonDesignConfig.getStringList("heatManagement.coolingItemsUsed")));
        if (cannonDesign.getItemCooling().size() != cannonDesign.getItemCoolingUsed().size())
            plugin.logSevere("CoolingItemsUsed and CoolingItems lists must have the same size. Check if both lists have the same number of entries");

        // overloading stuff
        cannonDesign.setOverloadingEnabled(cannonDesignConfig.getBoolean("overloading.enabled",false));
        cannonDesign.setOverloadingRealMode(cannonDesignConfig.getBoolean("overloading.realMode",false));
        cannonDesign.setOverloadingExponent(cannonDesignConfig.getDouble("overloading.exponent",1));
        cannonDesign.setOverloadingChangeInc(cannonDesignConfig.getDouble("overloading.chanceInc",0.1));
        cannonDesign.setOverloadingMaxOverloadableGunpowder(cannonDesignConfig.getInt("overloading.maxOverloadableGunpowder",3));
        cannonDesign.setOverloadingChanceOfExplosionPerGunpowder(cannonDesignConfig.getDouble("overloading.chanceOfExplosionPerGunpowder",0.01));
        cannonDesign.setOverloadingDependsOfTemperature(cannonDesignConfig.getBoolean("overloading.dependsOfTemperature",false));

        //economy
        String defaultKey = Config.getInstance().isEconomyEnabled() ? "cannons:vault" : "cannons:empty";
		Key economyKey = Key.from(cannonDesignConfig.getString("economy.type", defaultKey));
		cannonDesign.setEconomyType(economyKey);

		BExchanger buildCost = ExchangeLoader.of(economyKey, cannonDesignConfig, "economy.buildingCosts", Exchanger.Type.WITHDRAW);
        cannonDesign.setEconomyBuildingCost(buildCost);

		BExchanger dismantlingRefund = ExchangeLoader.of(economyKey, cannonDesignConfig, "economy.dismantlingRefund", Exchanger.Type.DEPOSIT);
        cannonDesign.setEconomyDismantlingRefund(dismantlingRefund);

		BExchanger destroyRefund = ExchangeLoader.of(economyKey, cannonDesignConfig, "economy.destructionRefund", Exchanger.Type.DEPOSIT);
        cannonDesign.setEconomyDestructionRefund(destroyRefund);

        // realisticBehaviour
		cannonDesign.setFiringItemRequired(cannonDesignConfig.getBoolean("realisticBehaviour.isFiringItemRequired", false));
        cannonDesign.setSootPerGunpowder(cannonDesignConfig.getDouble("realisticBehaviour.sootPerGunpowder", 0.0));
        cannonDesign.setProjectilePushing(cannonDesignConfig.getInt("realisticBehaviour.projectilePushing", 0));
		cannonDesign.setHasRecoil(cannonDesignConfig.getBoolean("realisticBehaviour.hasRecoil", false));
		cannonDesign.setFrontloader(cannonDesignConfig.getBoolean("realisticBehaviour.isFrontloader", false));
		cannonDesign.setRotatable(cannonDesignConfig.getBoolean("realisticBehaviour.isRotatable", false));
        cannonDesign.setMassOfCannon(cannonDesignConfig.getInt("realisticBehaviour.massOfCannon", 1000));//What means 1000?
        cannonDesign.setStartingSoot(cannonDesignConfig.getInt("realisticBehaviour.startingSoot",10));
        cannonDesign.setExplodingLoadedCannons(cannonDesignConfig.getDouble("realisticBehaviour.explodingLoadedCannon",2.0));
        cannonDesign.setFireAfterLoading(cannonDesignConfig.getBoolean("realisticBehaviour.fireAfterLoading", false));
		cannonDesign.setDismantlingDelay(cannonDesignConfig.getDouble("realisticBehaviour.dismantlingDelay", 1.75));

		// permissions
		cannonDesign.setPermissionBuild(cannonDesignConfig.getString("permissions.build", "cannons.player.build"));
		cannonDesign.setPermissionDismantle(cannonDesignConfig.getString("permissions.dismantle", "cannons.player.dismantle"));
        cannonDesign.setPermissionRename(cannonDesignConfig.getString("permissions.rename", "cannons.player.rename"));
		cannonDesign.setPermissionLoad(cannonDesignConfig.getString("permissions.load", "cannons.player.load"));
		cannonDesign.setPermissionFire(cannonDesignConfig.getString("permissions.fire", "cannons.player.fire"));
        cannonDesign.setPermissionAdjust(cannonDesignConfig.getString("permissions.adjust", "cannons.player.adjust"));
		cannonDesign.setPermissionAutoaim(cannonDesignConfig.getString("permissions.autoaim", "cannons.player.autoaim"));
        cannonDesign.setPermissionObserver(cannonDesignConfig.getString("permissions.observer", "cannons.player.observer"));
		cannonDesign.setPermissionTargetTracking(cannonDesignConfig.getString("permissions.targetTracking", "cannons.player.targetTracking"));
		cannonDesign.setPermissionRedstone(cannonDesignConfig.getString("permissions.redstone", "cannons.player.redstone"));
        cannonDesign.setPermissionThermometer(cannonDesignConfig.getString("permissions.thermometer", "cannons.player.thermometer"));
        cannonDesign.setPermissionRamrod(cannonDesignConfig.getString("permissions.ramrod", "cannons.player.ramrod"));
		cannonDesign.setPermissionAutoreload(cannonDesignConfig.getString("permissions.autoreload", "cannons.player.autoreload"));
		cannonDesign.setPermissionSpreadMultiplier(cannonDesignConfig.getString("permissions.spreadMultiplier", "cannons.player.spreadMultiplier"));

		// accessRestriction
		cannonDesign.setAccessForOwnerOnly(cannonDesignConfig.getBoolean("accessRestriction.ownerOnly", false));

		// allowedProjectiles
		cannonDesign.setAllowedProjectiles(cannonDesignConfig.getStringList("allowedProjectiles"));

        // sounds
        cannonDesign.setSoundCreate(new SoundHolder(cannonDesignConfig.getString("sounds.create","BLOCK_ANVIL_LAND:1:0.5")));
        cannonDesign.setSoundDestroy(new SoundHolder(cannonDesignConfig.getString("sounds.destroy","ENTITY_ZOMBIE_ATTACK_IRON_DOOR:1:0.5")));
        cannonDesign.setSoundDismantle(new SoundHolder(cannonDesignConfig.getString("sounds.dismantle", "BLOCK_ANVIL_USE:1:0.5")));
        cannonDesign.setSoundAdjust(new SoundHolder(cannonDesignConfig.getString("sounds.adjust","ENTITY_IRON_GOLEM_STEP:1:0.5")));
        cannonDesign.setSoundIgnite(new SoundHolder(cannonDesignConfig.getString("sounds.ignite","ENTITY_TNT_PRIMED:5:1")));
        cannonDesign.setSoundFiring(new SoundHolder(cannonDesignConfig.getString("sounds.firing","ENTITY_GENERIC_EXPLODE:20:1.5")));
        cannonDesign.setSoundGunpowderLoading(new SoundHolder(cannonDesignConfig.getString("sounds.gunpowderLoading","BLOCK_SAND_HIT:1:1.5")));
        cannonDesign.setSoundGunpowderOverloading(new SoundHolder(cannonDesignConfig.getString("sounds.gunpowderOverloading","BLOCK_GRASS_HIT:1:1.5")));
        cannonDesign.setSoundCool(new SoundHolder(cannonDesignConfig.getString("sounds.cool","BLOCK_FIRE_EXTINGUISH:1:1")));
        cannonDesign.setSoundHot(new SoundHolder(cannonDesignConfig.getString("sounds.hot","BLOCK_FIRE_EXTINGUISH:1:1")));
        cannonDesign.setSoundRamrodCleaning(new SoundHolder(cannonDesignConfig.getString("sounds.ramrodCleaning","BLOCK_SNOW_HIT:0.5:0")));
        cannonDesign.setSoundRamrodCleaningDone(new SoundHolder(cannonDesignConfig.getString("sounds.ramrodCleaningDone","BLOCK_SNOW_HIT:0.5:1")));
        cannonDesign.setSoundRamrodPushing(new SoundHolder(cannonDesignConfig.getString("sounds.ramrodPushing","BLOCK_STONE_HIT:0.5:0")));
        cannonDesign.setSoundRamrodPushingDone(new SoundHolder(cannonDesignConfig.getString("sounds.ramrodPushingDone","BLOCK_ANVIL_LAND:0.5:0")));
        cannonDesign.setSoundThermometer(new SoundHolder(cannonDesignConfig.getString("sounds.thermometer","BLOCK_ANVIL_LAND:1:1")));
        cannonDesign.setSoundEnableAimingMode(new SoundHolder(cannonDesignConfig.getString("sounds.enableAimingMode","NONE:1:1")));
        cannonDesign.setSoundDisableAimingMode(new SoundHolder(cannonDesignConfig.getString("sounds.disableAimingMode","NONE:1:1")));
		cannonDesign.setSoundSelected(new SoundHolder(cannonDesignConfig.getString("sounds.selected","BLOCK_ANVIL_LAND:1:2")));

		// constructionBlocks
		cannonDesign.setSchematicBlockTypeIgnore(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.ignore", "minecraft:sand")));
		cannonDesign.setSchematicBlockTypeMuzzle(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.muzzle", "minecraft:snow_block")));
		cannonDesign.setSchematicBlockTypeFiringIndicator(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.firingIndicator", "minecraft:torch")));
		cannonDesign.setSchematicBlockTypeRotationCenter(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.rotationCenter", "minecraft:redstone_ore")));
		cannonDesign.setSchematicBlockTypeChestAndSign(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.chestAndSign", "minecraft:oak_wall_sign")));
		cannonDesign.setSchematicBlockTypeRedstoneTorch(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.redstoneTorch", "minecraft:redstone_torch")));
		cannonDesign.setSchematicBlockTypeRedstoneWireAndRepeater(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.restoneWireAndRepeater", "minecraft:repeater")));
		// RedstoneTrigger
		cannonDesign.setSchematicBlockTypeRedstoneTrigger(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.redstoneTrigger.schematic", "minecraft:lever")));
		cannonDesign.setIngameBlockTypeRedstoneTrigger(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.redstoneTrigger.ingame", "minecraft:stone_button")));
		// rightClickTrigger
		cannonDesign.setSchematicBlockTypeRightClickTrigger(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.rightClickTrigger.schematic", "minecraft:torch")));
		cannonDesign.setIngameBlockTypeRightClickTrigger(CannonsUtil.createBlockData(cannonDesignConfig.getString("constructionBlocks.rightClickTrigger.ingame", "minecraft:torch")));
		// protected Blocks
		cannonDesign.setSchematicBlockTypeProtected(ParseUtils.toBlockDataList(cannonDesignConfig.getStringList("constructionBlocks.protectedBlocks")));
	}

    private static BlockKey bk(BlockData data) {
        NamespacedKey key = data.getMaterial().getKey();
        return new BlockKey(key.getNamespace(), key.getKey());
    }

    private static boolean isSchematicValid(Schematic schematic) {
        try {
            SchematicWorldProcessorImpl.getProcessor().parseToMaterial(schematic);
            return true;
        } catch (Throwable throwable) {
            return false;
        }
    }

	/**
	 * loads the schematic of the config file
	 * @param cannonDesign design of the cannon
	 * @param schematicFile path of the schematic file
	 */
    private static final BlockFace[] HORIZONTALS = new BlockFace[] {BlockFace.EAST, BlockFace.SOUTH, BlockFace.NORTH, BlockFace.WEST};
	private boolean loadDesignSchematic(CannonDesign cannonDesign, String schematicFile) {
        long startTime = System.nanoTime();

        String schematicPath = getPath() + schematicFile;
        File schemFile = new File(schematicPath);

		// convert all schematic blocks from the config to BaseBlocks so they
		// can be rotated
        BlockData blockIgnore = cannonDesign.getSchematicBlockTypeIgnore();
        BlockData blockMuzzle = cannonDesign.getSchematicBlockTypeMuzzle();
        BlockData blockFiringIndicator = cannonDesign.getSchematicBlockTypeFiringIndicator();
        BlockData blockRotationCenter = cannonDesign.getSchematicBlockTypeRotationCenter();
        BlockData blockChestAndSign = cannonDesign.getSchematicBlockTypeChestAndSign();
        BlockData blockRedstoneTorch = cannonDesign.getSchematicBlockTypeRedstoneTorch();
        BlockData blockRedstoneWireAndRepeater = cannonDesign.getSchematicBlockTypeRedstoneWireAndRepeater();
        BlockData blockRedstoneTrigger = cannonDesign.getSchematicBlockTypeRedstoneTrigger();
        BlockData blockRightClickTrigger = cannonDesign.getSchematicBlockTypeRightClickTrigger();
        BlockData replaceRedstoneTrigger = cannonDesign.getIngameBlockTypeRedstoneTrigger();
        BlockData replaceRightClickTrigger = cannonDesign.getIngameBlockTypeRightClickTrigger();
        List<BlockData> blockProtectedList = new ArrayList<>(cannonDesign.getSchematicBlockTypeProtected());
        List<BlockKey> keysBlocksProtected = blockProtectedList.stream().map(DesignStorage::bk).toList();

        // get facing of the cannon
        BlockFace cannonDirection = cannonDesign.getDefaultHorizontalFacing();

		//plugin.logDebug("design: " + schematicFile);
        Schematic blocks = getSchematic(schemFile, bk(blockIgnore));
        if (!isSchematicValid(blocks)) {
            plugin.logSevere("Schematic " + schematicFile + " is invalid, maybe it has custom blocks whose plugin has been removed?");
            return false;
        }
        if (blocks == null) return false;

        ICoord max = blocks.getMax();
        ICoord min = blocks.getMin();
        int width = max.x() - min.x() + 1;
        int height = max.y() - min.y() + 1;
        int length = max.z() - min.z() + 1;

        for (int i = 0; i < 4; i++) {
            // create CannonBlocks entry
            CannonBlocks cannonBlocks = new CannonBlocks();
            List<IBlock> filteredSchematic = new ArrayList<>();

            // to set the muzzle location the maximum and mininum x, y, z values
            // of all muzzle blocks have to be found
            Vector minMuzzle = new Vector(0, 0, 0);
            Vector maxMuzzle = new Vector(0, 0, 0);
            boolean firstEntryMuzzle = true;

            // to set the rotation Center maximum and mininum x, y, z values
            // of all rotation blocks have to be found
            // setting max to the size of the marked area is a good approximation
            // if no rotationblock is given
            Vector minRotation = new Vector(0, 0, 0);
            Vector maxRotation = new Vector(width, height, length);
            boolean firstEntryRotation = true;

            for (IBlock sblock : blocks) {
                int x = sblock.x();
                int y = sblock.y();
                int z = sblock.z();

                // #############  find the min and max for muzzle blocks so the
                // cannonball is fired from the middle
                BlockKey key = sblock.key();
                if (key.equals(bk(blockMuzzle))) {
                    // reset for the first entry
                    if (firstEntryMuzzle) {
                        firstEntryMuzzle = false;
                        minMuzzle = new Vector(x, y, z);
                        maxMuzzle = new Vector(x, y, z);
                    } else {
                        setMinimum(x, y, z, minMuzzle);
                        setMaximum(x, y, z, maxMuzzle);
                    }
                    //muzzle blocks need to be air - else the projectile would spawn in a block
                    //cannonBlocks.addAllCannonBlocks(new SimpleBlock(x, y, z, Material.AIR));
                    filteredSchematic.add(new FileBlock(x, y, z, BlockKey.mc("air")));
                }
                // #############  find the min and max for rotation blocks
                else if (key.equals(bk(blockRotationCenter))) {
                    // reset for the first entry
                    if (firstEntryRotation) {
                        firstEntryRotation = false;
                        minRotation = new Vector(x, y, z);
                        maxRotation= new Vector(x, y, z);
                    } else {
                        setMinimum(x, y, z, minRotation);
                        setMaximum(x, y, z, maxRotation);
                    }
                }
                // #############  redstoneTorch
                else if (key.equals(bk(blockRedstoneTorch))) {
                    cannonBlocks.addRedstoneTorch(new Vector(x, y, z));
                }
                    // #############  redstoneWire and Repeater
                else if (key.equals(bk(blockRedstoneWireAndRepeater))) {
                    cannonBlocks.addRedstoneWiresAndRepeater(new SimpleBlock(x, y, z, Material.REPEATER));
                }
                    // #############  redstoneTrigger
                else if (!key.equals(bk(blockRedstoneTrigger))) {
                    if (key.equals(bk(blockRightClickTrigger))) {
                        cannonBlocks.addRightClickTrigger(new Vector(x, y, z));
                        //can be also a sign
                        if (key.equals(bk(blockChestAndSign)))
                            // the id does not matter, but the data is important for signs
                            cannonBlocks.addChestsAndSigns(new SimpleBlock(x, y, z, key)); //Material.WALL_SIGN
                        // firing blocks are also part of the cannon are
                        // part of the cannon
                        //cannonBlocks.addAllCannonBlocks(new SimpleBlock(x, y, z, replaceRightClickTrigger));
                        filteredSchematic.add(new FileBlock(x, y, z, bk(replaceRightClickTrigger)));
                        // this can be a destructible block
                        if (!isInList(keysBlocksProtected, key))
                            cannonBlocks.addDestructibleBlocks(new Vector(x, y, z));
                    }
                    // #############  chests and signs
                    else if (key.equals(bk(blockChestAndSign))) {
                        // the id does not matter, but the data is important for signs
                        cannonBlocks.addChestsAndSigns(new SimpleBlock(x, y, z, key)); //Material.WALL_SIGN
                    }
                    // #############  loading Interface is a cannonblock that is non of
                    // the previous blocks
                    else {
                        // all remaining blocks are loading interface or cannonBlocks
                        cannonBlocks.addBarrelBlocks(new Vector(x, y, z));
                        //cannonBlocks.addAllCannonBlocks(new SimpleBlock(x, y, z, key));
                        filteredSchematic.add(new FileBlock(x, y, z, key));
                        // this can be a destructible block
                        if (!isInList(keysBlocksProtected, key))
                            cannonBlocks.addDestructibleBlocks(new Vector(x, y, z));
                    }
                }
                // #############  rightClickTrigger
                else {
                    cannonBlocks.addRedstoneTrigger(new Vector(x, y, z));
                    // buttons or levers are part of the cannon
                    //cannonBlocks.addAllCannonBlocks(new SimpleBlock(x, y, z, replaceRedstoneTrigger));
                    filteredSchematic.add(new FileBlock(x, y, z, bk(replaceRedstoneTrigger)));
                    // this can be a destructible block
                    if (!isInList(keysBlocksProtected, key))
                        cannonBlocks.addDestructibleBlocks(new Vector(x, y, z));
                }


                // #############  firingIndicator
                // can be everywhere on the cannon
                if (key.equals(bk(blockFiringIndicator)))
                    cannonBlocks.addFiringIndicator(new Vector(x, y, z));
            }

            // calculate the muzzle location
            maxMuzzle.add(new Vector(1, 1, 1));
            cannonBlocks.setMuzzle(maxMuzzle.add(minMuzzle).multiply(0.5));

            // calculate the rotation Center
            maxRotation.add(new Vector(1, 1, 1));
            cannonBlocks.setRotationCenter(maxRotation.add(maxRotation).multiply(0.5));

            //set the muzzle location
            Vector compensation = new Vector(cannonBlocks.getMuzzle().getBlockX(), cannonBlocks.getMuzzle().getBlockY(), cannonBlocks.getMuzzle().getBlockZ());

            List<IBlock> actualSchematic = new ArrayList<>();
            for (IBlock iblock : filteredSchematic)
                actualSchematic.add(
                    iblock.add(
                        new FileCoord(
                            -compensation.getBlockX(),
                            -compensation.getBlockY(),
                            -compensation.getBlockZ()
                        )
                    )
                );

            for (Vector block : cannonBlocks.getBarrelBlocks())
                block.subtract(compensation);
            for (SimpleBlock block : cannonBlocks.getChestsAndSigns())
                block.directSubtract(compensation);
            for (Vector block : cannonBlocks.getRedstoneTorches())
                block.subtract(compensation);
            for (SimpleBlock block : cannonBlocks.getRedstoneWiresAndRepeater())
                block.directSubtract(compensation);
            for (Vector block : cannonBlocks.getRedstoneTrigger())
                block.subtract(compensation);
            for (Vector block : cannonBlocks.getRightClickTrigger())
                block.subtract(compensation);
            for (Vector block : cannonBlocks.getFiringIndicator())
                block.subtract(compensation);
            for (Vector block : cannonBlocks.getDestructibleBlocks())
                block.subtract(compensation);
            cannonBlocks.getMuzzle().subtract(compensation);
            cannonBlocks.getRotationCenter().subtract(compensation);

            // add blocks to the HashMap
            cannonDesign.putCannonBlockMap(cannonDirection, cannonBlocks);
            cannonDesign.putSchematicMap(cannonDirection, new FileSchematic(actualSchematic));

            //rotate blocks for the next iteration
            blockIgnore = CannonsUtil.roateBlockFacingClockwise(blockIgnore);
            blockMuzzle = CannonsUtil.roateBlockFacingClockwise(blockMuzzle);
            blockFiringIndicator = CannonsUtil.roateBlockFacingClockwise(blockFiringIndicator);
            blockRotationCenter = CannonsUtil.roateBlockFacingClockwise(blockRotationCenter);
            blockChestAndSign = CannonsUtil.roateBlockFacingClockwise(blockChestAndSign);
            blockRedstoneTorch = CannonsUtil.roateBlockFacingClockwise(blockRedstoneTorch);
            blockRedstoneTrigger = CannonsUtil.roateBlockFacingClockwise(blockRedstoneTrigger);
            blockRightClickTrigger = CannonsUtil.roateBlockFacingClockwise(blockRightClickTrigger);
            replaceRedstoneTrigger = CannonsUtil.roateBlockFacingClockwise(replaceRedstoneTrigger);
            replaceRightClickTrigger = CannonsUtil.roateBlockFacingClockwise(replaceRightClickTrigger);

            blockProtectedList = blockProtectedList.stream().map(CannonsUtil::roateBlockFacingClockwise).toList();

            //rotate schematic blocks
            ArrayList<IBlock> newList = new ArrayList<>();
            for (IBlock simpleBlock : blocks){
                newList.add(
                    new FileBlock(
                        -simpleBlock.z(),
                        simpleBlock.y(),
                        simpleBlock.x(),
                        simpleBlock.key()
                    )
                );
            }

            blocks = new FileSchematic(newList);

            cannonDirection = CannonsUtil.roatateFace(cannonDirection);
        }

        plugin.logDebug("Time to load designs: " + new DecimalFormat("0.00").format((System.nanoTime() - startTime)/1000000.0) + "ms");

        return true;
	}

	private void setMinimum(int x, int y, int z, Vector min) {
		if (x < min.getBlockX()) min.setX(x);
		if (y < min.getBlockY()) min.setY(y);
		if (z < min.getBlockZ()) min.setZ(z);
	}

	private void setMaximum(int x, int y, int z, Vector max) {
		if (x > max.getBlockX()) max.setX(x);
		if (y > max.getBlockY()) max.setY(y);
		if (z > max.getBlockZ()) max.setZ(z);
	}

	/**
	 * copy the default designs from the .jar to the disk
	 */
	private void copyDefaultDesigns()
	{
		copyFile("classic");
        copyFile("mortar");
        copyFile("ironCannon");
		copyFile("sentry");
	}

    /**
     * Copys the given .yml and .schematic from the .jar to the disk
     * @param fileName - name of the design file
     */
    private void copyFile(String fileName)
    {
        File YmlFile = new File(plugin.getDataFolder(), "designs/" + fileName + ".yml");
        File SchematicFile = new File(plugin.getDataFolder(), "designs/" + fileName + ".schematic");

        SchematicFile.getParentFile().mkdirs();
        if (!YmlFile.exists())
        {
            CannonsUtil.copyFile(plugin.getResource("designs/" + fileName + ".yml"), YmlFile);
        }
        if (!SchematicFile.exists())
        {
            CannonsUtil.copyFile(plugin.getResource("designs/" + fileName + ".schematic"), SchematicFile);
        }
    }
	
	private <T> boolean isInList(List<T> list, T block)
	{
		if (block == null) return true;
		
		for (T listBlock : list)
		{
			if (listBlock != null && listBlock.equals(block))
				return true;
		}
		return false;
	}
	
	public static String getPath()
	{
		// Directory path here
		return "plugins/Cannons/designs/";
	}

    /**
	 * returns the cannon design of the cannon
	 * @param cannon the cannon
	 * @return design of cannon
	 */
	public CannonDesign getDesign(Cannon cannon)
	{
		return getDesign(cannon.getDesignID());
	}
	
	/**
	 * returns the cannon design by its id
	 * @param designId Name of the design
	 * @return cannon design
	 */
	public CannonDesign getDesign(String designId)
	{
		for (CannonDesign cannonDesign : cannonDesignList)
		{
			if (cannonDesign.getDesignID().equals(designId))
				return cannonDesign;
		}
		return null;
	}

	/**
	 * is there a cannon design with the give name
	 * @param name name of the design
	 * @return true if there is a cannon design with this name
     */
	public boolean hasDesign(String name){
		for (CannonDesign design : cannonDesignList){
			if (design.getDesignID().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

    public boolean isCannonBlockMaterial(Material material) {
		return material != Material.AIR && cannonBlockMaterials.contains(material);
	}


    private static final SchematicLoader[] loaders = new SchematicLoader[] {
        new WorldEditFormat(),
        new VaanFormat()
    };

	private Schematic getSchematic(File file, BlockKey blockIgnore) {
        for (SchematicLoader loader : loaders) {
            try {
                Schematic schm = loader.load(file);
                if (schm == null) continue;
                List<IBlock> list = schm.positions().stream().filter(it -> !it.key().equals(blockIgnore) && !it.key().key().equals("air")).toList();
                return new FileSchematic(list);
            } catch (Throwable ignored) {}
        }

		plugin.logSevere("Couldn't load " + file.getPath());
        return null;
	}
}
