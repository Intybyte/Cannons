package at.pavlov.bukkit.cannons;

import at.pavlov.bukkit.container.BukkitCannonBlocks;
import at.pavlov.bukkit.container.BukkitSoundHolder;
import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.bukkit.container.BukkitBlock;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.bukkit.projectile.BukkitProjectile;
import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.container.location.CannonVector;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

@Data public class CannonDesign {
	//general
	private String designID;
	private String designName;
    private String messageName;
    private String description;
    private boolean lastUserBecomesOwner;
	
	//sign
	private boolean signRequired;
	
	//ammunition_consumption
	private String gunpowderName;
	private BukkitItemHolder gunpowderType;
    private boolean gunpowderNeeded;
    private boolean gunpowderConsumption;
    private boolean projectileConsumption;
	private boolean ammoInfiniteForPlayer;
    private boolean ammoInfiniteForRedstone;
    private boolean autoreloadRedstone;
	private boolean removeChargeAfterFiring;
	private boolean autoloadChargeWhenLoadingProjectile;
	private boolean preloaded;

    //barrelProperties
	private int maxLoadableGunpowder;
	private double multiplierVelocity;
	private double spreadOfCannon;
	
	//timings
	private double blastConfusion;
	private double fuseBurnTime;
	private double fuseBurnTimeRandomness;
    private double barrelCooldownTime;
	private double loadTime;
	
    //angles
	private BlockFace defaultHorizontalFacing;
	private double defaultVerticalAngle;
	private double maxHorizontalAngle;
	private double minHorizontalAngle;
	private double maxVerticalAngle;
	private double minVerticalAngle;
    private double maxHorizontalAngleOnShip;
    private double minHorizontalAngleOnShip;
    private double maxVerticalAngleOnShip;
    private double minVerticalAngleOnShip;
	private double angleStepSize;
	private double angleLargeStepSize;
	private int angleUpdateSpeed;
	private boolean angleUpdateMessage;

    //impactPredictor
    private boolean predictorEnabled;
    private int predictorDelay;         //in ms
    private int predictorUpdate;        //in ms

	//sentry
	private boolean sentry;
	private boolean sentryIndirectFire;
    private int sentryMinRange;
    private int sentryMaxRange;
	private double sentrySpread;
	private int sentryUpdateTime;				//in ms
    private int sentrySwapTime;				    //in ms

	//linkCannons
	private boolean linkCannonsEnabled;
	private int linkCannonsDistance;

    //heatManagment
    private boolean heatManagementEnabled;
    private boolean automaticTemperatureControl;
    private double burnDamage;
    private double burnSlowing;
    private double heatIncreasePerGunpowder;
    private double coolingCoefficient;
    private double coolingAmount;
    private boolean automaticCooling;
    private double warningTemperature;
    private double criticalTemperature;
    private double maximumTemperature;
    private List<BukkitItemHolder> itemCooling = new ArrayList<>();
    private List<BukkitItemHolder> itemCoolingUsed = new ArrayList<>();

    //Overloading stuff
    private boolean overloadingEnabled;
    private boolean overloadingRealMode;
    private double overloadingExponent;
    private double overloadingChanceInc;
    private int overloadingMaxOverloadableGunpowder;
    private double overloadingChanceOfExplosionPerGunpowder;
    private boolean overloadingDependsOfTemperature;

	private double economyBuildingCost;
	private double economyDismantlingRefund;
	private double economyDestructionRefund;

	//realisticBehaviour
	private boolean FiringItemRequired;
    private double sootPerGunpowder;
    private int projectilePushing;
	private boolean hasRecoil;
	private boolean frontloader;
	private boolean rotable;
    private int massOfCannon;
    private int startingSoot;
    private double explodingLoadedCannons;
    private boolean fireAfterLoading;
    private double dismantlingDelay;
	
	//permissions
	private String permissionBuild;
	private String permissionDismantle;
    private String permissionRename;
	private String permissionLoad;
	private String permissionFire;
	private String permissionAdjust;
	private String permissionAutoaim;
    private String permissionObserver;
	private String permissionTargetTracking;
	private String permissionRedstone;
    private String permissionThermometer;
    private String permissionRamrod;
	private String permissionAutoreload;
	private String permissionSpreadMultiplier;
	
	//accessRestriction
	private boolean accessForOwnerOnly;
	
	//allowedProjectile
	private List<String> allowedProjectiles;

    //sounds
    private BukkitSoundHolder soundCreate;
    private BukkitSoundHolder soundDestroy;
	private BukkitSoundHolder soundDismantle;
    private BukkitSoundHolder soundAdjust;
    private BukkitSoundHolder soundIgnite;
    private BukkitSoundHolder soundFiring;
    private BukkitSoundHolder soundGunpowderLoading;
    private BukkitSoundHolder soundGunpowderOverloading;
    private BukkitSoundHolder soundCool;
    private BukkitSoundHolder soundHot;
    private BukkitSoundHolder soundRamrodCleaning;
    private BukkitSoundHolder soundRamrodCleaningDone;
    private BukkitSoundHolder soundRamrodPushing;
    private BukkitSoundHolder soundRamrodPushingDone;
    private BukkitSoundHolder soundThermometer;
    private BukkitSoundHolder soundEnableAimingMode;
    private BukkitSoundHolder soundDisableAimingMode;
	private BukkitSoundHolder soundSelected;

	
	//constructionblocks:
	private BlockData schematicBlockTypeIgnore;     				//this block this is ignored in the schematic file
    private BlockData schematicBlockTypeMuzzle;					//location of the muzzle
    private BlockData schematicBlockTypeRotationCenter;			//location of the roatation
    private BlockData schematicBlockTypeChestAndSign;				//locations of the chest and sign
    private BlockData schematicBlockTypeRedstoneTorch;				//locations of the redstone torches
    private BlockData schematicBlockTypeRedstoneWireAndRepeater;	//locations of the redstone wires and repeaters
    private BlockData schematicBlockTypeRedstoneTrigger; 			//locations of button or levers
    private BlockData ingameBlockTypeRedstoneTrigger;    			//block which is placed instead of the place holder
    private BlockData schematicBlockTypeRightClickTrigger; 		//locations of the right click trigger
    private BlockData ingameBlockTypeRightClickTrigger;   			//block type of the tigger in game
    private BlockData schematicBlockTypeFiringIndicator;			//location of the firing indicator
    private List<BlockData> schematicBlockTypeProtected;				//list of blocks that are protected from explosions (e.g. buttons)
    
    //cannon design block lists for every direction (NORTH, EAST, SOUTH, WEST)
    private final HashMap<BlockFace, BukkitCannonBlocks> cannonBlockMap = new HashMap<>();
	private final EnumSet<Material> allowedMaterials = EnumSet.noneOf(Material.class);


    
    /**
     * returns the rotation center of a cannon design
     */
    public Location getRotationCenter(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
            CannonVector vector = cannonBlocks.getRotationCenter().clone().add(cannon.getOffset());
    		return VectorUtils.toLoc(cannon.getWorldBukkit(), vector);
    	}

    	CannonLogger.getLogger().info("missing rotation center for cannon design " + cannon.getCannonName());
    	return VectorUtils.toLoc(cannon.getWorldBukkit(), cannon.getOffset());
    }


    /**
     * returns the muzzle location
     */
    public Location getMuzzle(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
            CannonVector vector = cannonBlocks.getMuzzle().clone().add(cannon.getOffset());
    		return VectorUtils.toLoc(cannon.getWorldBukkit(), vector);
    	}

    	CannonLogger.getLogger().info("missing muzzle location for cannon design " + cannon.getCannonName());
    	return VectorUtils.toLoc(cannon.getWorldBukkit(), cannon.getOffset());
    }
    
    /**
     * returns one trigger location
     * @param cannon the used cannon
     * @return the firing trigger of the cannon - can be null if the cannon has no trigger
     */
    public Location getFiringTrigger(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null && cannonBlocks.getFiringTrigger() != null)
    	{
            CannonVector vector = cannonBlocks.getFiringTrigger().clone().add(cannon.getOffset());
            return VectorUtils.toLoc(cannon.getWorldBukkit(), vector);
    	}
    	return null;
    }
    
    /**
     * returns a list of all cannonBlocks
     * @param cannonDirection - the direction the cannon is facing
     * @return List of cannon blocks
     */
    public List<BukkitBlock> getAllCannonBlocks(BlockFace cannonDirection)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannonDirection);
    	if (cannonBlocks != null)
    	{
    		return cannonBlocks.getAllCannonBlocks();
    	}
    	
    	return new ArrayList<>();
    }


    /**
     * returns a list of all cannonBlocks
     */
    public List<Location> getAllCannonBlocks(CannonBukkit cannon)
    {
        BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (BukkitBlock block : cannonBlocks.getAllCannonBlocks())
            {
                CannonVector vect = block.toVector();
                CannonVector calc = vect.clone().add(cannon.getOffset());
                locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), calc));
            }
        }
        return locList;
    }

    /**
     * returns a list of all destructible blocks
     */
    public List<Location> getDestructibleBlocks(CannonBukkit cannon)
    {
     	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getDestructibleBlocks())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all firingIndicator blocks
     */
    public List<Location> getFiringIndicator(CannonBukkit cannon)
    {
     	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getFiringIndicator())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all loading interface blocks
     */
    public List<Location> getLoadingInterface(CannonBukkit cannon)
    {
        BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (CannonVector vect : cannonBlocks.getBarrelBlocks())
            {
                CannonVector vector = vect.clone().add(cannon.getOffset());
                locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
            }
        }
        return locList;
    }

    /**
     * returns a list of all barrel blocks
     */
    public List<Location> getBarrelBlocks(CannonBukkit cannon)
    {
        BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (CannonVector vect : cannonBlocks.getBarrelBlocks())
            {
                CannonVector vector = vect.clone().add(cannon.getOffset());
                locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
            }
        }
        return locList;
    }
    
    /**
     * returns a list of all right click trigger blocks
     */
    public List<Location> getRightClickTrigger(CannonBukkit cannon)
    {
     	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRightClickTrigger())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone trigger blocks
     */
    public List<Location> getRedstoneTrigger(CannonBukkit cannon)
    {
     	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRedstoneTrigger())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all chest/sign blocks
     */
    public List<Location> getChestsAndSigns(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (BukkitBlock block : cannonBlocks.getChestsAndSigns())
    		{
    			locList.add(block.toLocation(cannon.getWorldBukkit(), cannon.getOffset()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone torch blocks
     */
    public List<Location> getRedstoneTorches(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRedstoneTorches())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(VectorUtils.toLoc(cannon.getWorldBukkit(), vector));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone wire/repeater blocks
     */
    public List<Location> getRedstoneWireAndRepeater(CannonBukkit cannon)
    {
    	BukkitCannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (BukkitBlock block : cannonBlocks.getRedstoneWiresAndRepeater())
    		{
    			locList.add(block.toLocation(cannon.getWorldBukkit(),cannon.getOffset()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns true if the projectile has the same Id of a allowed projectile
     * @param projectile projectile to load
     * @return true if the projectile can be loaded in this type of cannon
     */
    public boolean canLoad(BukkitProjectile projectile)
    {
    	for (String p : allowedProjectiles)
    	{
    		if (projectile.getProjectileID().equals(p))
    			return true;
    	}
    	
    	return false;
    }

	/**
	 * Normal means without overloading stuff
	 * @return maxLoadableGunpowder
	 */
	public int getMaxLoadableGunpowderNormal()
	{
		return maxLoadableGunpowder;
	}
	/**
	 * Absolute means maximum loadable gunpowder
	 * @return if overloading stuff is enabled for this cannon, returns maxLoadableGunpowder+overloading_maxOverloadableGunpowder, else returns maxLoadableGunpowder
	 */
	public int getMaxLoadableGunpowderOverloaded()
	{
		if(overloadingEnabled)
            return maxLoadableGunpowder+overloadingMaxOverloadableGunpowder;
		else
            return getMaxLoadableGunpowderNormal();
	}

	public void putCannonBlockMap(BlockFace cannonDirection, BukkitCannonBlocks blocks) {
		for (var block : blocks.getAllCannonBlocks()) {
			allowedMaterials.add(block.getBlockData().getMaterial());
		}

		cannonBlockMap.put(cannonDirection, blocks);
	}

	public boolean isAllowedMaterial(Material m) {
		return allowedMaterials.contains(m);
	}
	
	@Override
	public String toString()
	{
		return "designID:" + designID + " name:" + designName + " blocks:" + getAllCannonBlocks(BlockFace.NORTH).size();
	}

    /**
     * is this Item a cooling tool to cool down a cannon
     * @param item - item to check
     * @return - true if this item is in the list of cooling items
     */
    public boolean isCoolingTool(ItemStack item)
    {
    	//todo rework tool properties
        for (BukkitItemHolder mat : itemCooling)
        {
            if (mat.equalsFuzzy(item))
                return true;
        }
        return false;
    }

    /**
     * returns the used used item. E.g. a water bucket will be an empty bucket.
     * @param item - the item used for the event
     * @return the new item which replaces the old one
     */
    public ItemStack getCoolingToolUsed(ItemStack item)
    {
        for (int i=0; i < itemCooling.size(); i++)
        {
			//todo rework tool properties
            if (itemCooling.get(i).equalsFuzzy(item))
            {
                return itemCoolingUsed.get(i).toItemStack(item.getAmount());
            }
        }
        return null;
    }
}
