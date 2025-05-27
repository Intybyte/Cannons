package at.pavlov.cannons.cannon;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SimpleBlock;
import at.pavlov.cannons.container.SoundHolder;
import at.pavlov.cannons.exchange.BExchanger;
import at.pavlov.cannons.projectile.Projectile;
import at.pavlov.internal.Key;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
	private boolean isSignRequired; 
	
	//ammunition_consumption
	private String gunpowderName;
	private ItemHolder gunpowderType;
    private boolean needsGunpowder;
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
    private List<ItemHolder> itemCooling = new ArrayList<>();
    private List<ItemHolder> itemCoolingUsed = new ArrayList<>();

    //Overloading stuff
    private boolean overloadingEnabled;
    private boolean overloadingRealMode;
    private double overloadingExponent;
    private double overloadingChanceInc;
    private int overloadingMaxOverloadableGunpowder;
    private double overloadingChanceOfExplosionPerGunpowder;
    private boolean overloadingDependsOfTemperature;

	//economy handling
	private Key economyType;
	private BExchanger economyBuildingCost;
	private BExchanger economyDismantlingRefund;
	private BExchanger economyDestructionRefund;

	//realisticBehaviour
	private boolean FiringItemRequired;
    private double sootPerGunpowder;
    private int projectilePushing;
	private boolean hasRecoil;
	private boolean isFrontloader;
	private boolean isRotatable;
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
    private SoundHolder soundCreate;
    private SoundHolder soundDestroy;
	private SoundHolder soundDismantle;
    private SoundHolder soundAdjust;
    private SoundHolder soundIgnite;
    private SoundHolder soundFiring;
    private SoundHolder soundGunpowderLoading;
    private SoundHolder soundGunpowderOverloading;
    private SoundHolder soundCool;
    private SoundHolder soundHot;
    private SoundHolder soundRamrodCleaning;
    private SoundHolder soundRamrodCleaningDone;
    private SoundHolder soundRamrodPushing;
    private SoundHolder soundRamrodPushingDone;
    private SoundHolder soundThermometer;
    private SoundHolder soundEnableAimingMode;
    private SoundHolder soundDisableAimingMode;
	private SoundHolder soundSelected;

	
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
    private final HashMap<BlockFace, CannonBlocks> cannonBlockMap = new HashMap<>();
	private final EnumSet<Material> allowedMaterials = EnumSet.noneOf(Material.class);


    
    /**
     * returns the rotation center of a cannon design
     * @param cannon
     * @return
     */
    public Location getRotationCenter(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
    		return cannonBlocks.getRotationCenter().clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit());
    	}

    	Cannons.logger().info("missing rotation center for cannon design " + cannon.getCannonName());
    	return cannon.getOffset().toLocation(cannon.getWorldBukkit());
    }


    /**
     * returns the muzzle location
     * @param cannon
     * @return
     */
    public Location getMuzzle(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
    		return cannonBlocks.getMuzzle().clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit());
    	}

    	Cannons.logger().info("missing muzzle location for cannon design " + cannon.getCannonName());
    	return cannon.getOffset().toLocation(cannon.getWorldBukkit());
    }
    
    /**
     * returns one trigger location
     * @param cannon the used cannon
     * @return the firing trigger of the cannon - can be null if the cannon has no trigger
     */
    public Location getFiringTrigger(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null && cannonBlocks.getFiringTrigger() != null)
    	{
            return cannonBlocks.getFiringTrigger().clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit());
    	}
    	return null;
    }
    
    /**
     * returns a list of all cannonBlocks
     * @param cannonDirection - the direction the cannon is facing
     * @return List of cannon blocks
     */
    public List<SimpleBlock> getAllCannonBlocks(BlockFace cannonDirection)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannonDirection);
    	if (cannonBlocks != null)
    	{
    		return cannonBlocks.getAllCannonBlocks();
    	}
    	
    	return new ArrayList<>();
    }


    /**
     * returns a list of all cannonBlocks
     * @param cannon
     * @return
     */
    public List<Location> getAllCannonBlocks(Cannon cannon)
    {
        CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (SimpleBlock block : cannonBlocks.getAllCannonBlocks())
            {
                Vector vect = block.toVector();
                locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
            }
        }
        return locList;
    }

    /**
     * returns a list of all destructible blocks
     * @param cannon
     * @return
     */
    public List<Location> getDestructibleBlocks(Cannon cannon)
    {
     	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (Vector vect : cannonBlocks.getDestructibleBlocks())
    		{
    			locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all firingIndicator blocks
     * @param cannon
     * @return
     */
    public List<Location> getFiringIndicator(Cannon cannon)
    {
     	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (Vector vect : cannonBlocks.getFiringIndicator())
    		{
    			locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all loading interface blocks
     * @param cannon
     * @return
     */
    public List<Location> getLoadingInterface(Cannon cannon)
    {
        CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (Vector vect : cannonBlocks.getBarrelBlocks())
            {
                locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
            }
        }
        return locList;
    }

    /**
     * returns a list of all barrel blocks
     * @param cannon
     * @return
     */
    public List<Location> getBarrelBlocks(Cannon cannon)
    {
        CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Location> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (Vector vect : cannonBlocks.getBarrelBlocks())
            {
                locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
            }
        }
        return locList;
    }
    
    /**
     * returns a list of all right click trigger blocks
     * @param cannon
     * @return
     */
    public List<Location> getRightClickTrigger(Cannon cannon)
    {
     	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (Vector vect : cannonBlocks.getRightClickTrigger())
    		{
    			locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone trigger blocks
     * @param cannon
     * @return
     */
    public List<Location> getRedstoneTrigger(Cannon cannon)
    {
     	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (Vector vect : cannonBlocks.getRedstoneTrigger())
    		{
    			locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all chest/sign blocks
     * @param cannon
     * @return
     */
    public List<Location> getChestsAndSigns(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (SimpleBlock block : cannonBlocks.getChestsAndSigns())
    		{
    			locList.add(block.toLocation(cannon.getWorldBukkit(), cannon.getOffset()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone torch blocks
     * @param cannon
     * @return
     */
    public List<Location> getRedstoneTorches(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (Vector vect : cannonBlocks.getRedstoneTorches())
    		{
    			locList.add(vect.clone().add(cannon.getOffset()).toLocation(cannon.getWorldBukkit()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone wire/repeater blocks
     * @param cannon
     * @return
     */
    public List<Location> getRedstoneWireAndRepeater(Cannon cannon)
    {
    	CannonBlocks cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Location> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (SimpleBlock block : cannonBlocks.getRedstoneWiresAndRepeater())
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
    public boolean canLoad(Projectile projectile)
    {
    	for (String p : allowedProjectiles)
    	{
    		if (projectile.getProjectileID().equals(p))
    			return true;
    	}
    	
    	return false;
    }

	public boolean isSignRequired()
	{
		return isSignRequired;
	}
	public void setSignRequired(boolean isSignRequired)
	{
		this.isSignRequired = isSignRequired;
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

	public double getMaxHorizontalAngleNormal()
	{
		return maxHorizontalAngle;
	}
	public void setMaxHorizontalAngleNormal(double maxHorizontalAngle)
	{
		this.maxHorizontalAngle = maxHorizontalAngle;
	}
	public double getMinHorizontalAngleNormal()
	{
		return minHorizontalAngle;
	}
	public void setMinHorizontalAngleNormal(double minHorizontalAngle)
	{
		this.minHorizontalAngle = minHorizontalAngle;
	}
	public double getMaxVerticalAngleNormal()
	{
		return maxVerticalAngle;
	}
	public void setMaxVerticalAngleNormal(double maxVerticalAngle)
	{
		this.maxVerticalAngle = maxVerticalAngle;
	}
	public double getMinVerticalAngleNormal()
	{
		return minVerticalAngle;
	}
	public void setMinVerticalAngleNormal(double minVerticalAngle)
	{
		this.minVerticalAngle = minVerticalAngle;
	}

	public boolean isFrontloader()
	{
		return isFrontloader;
	}
	public void setFrontloader(boolean isFrontloader)
	{
		this.isFrontloader = isFrontloader;
	}
	public boolean isRotatable()
	{
		return isRotatable;
	}
	public void setRotatable(boolean isRotatable)
	{
		this.isRotatable = isRotatable;
	}


	public void putCannonBlockMap(BlockFace cannonDirection, CannonBlocks blocks) {
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
        for (ItemHolder mat : itemCooling)
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

    public boolean isGunpowderNeeded() {
        return needsGunpowder;
    }

	public void setOverloadingChangeInc(double overloadingChanceInc) {
		this.overloadingChanceInc = overloadingChanceInc;
	}

	public double getOverloadingChangeInc() {
		return overloadingChanceInc;
	}
}
