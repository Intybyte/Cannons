package at.pavlov.internal.cannons.data;

import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.cannons.holders.CannonMainDataHolder;
import at.pavlov.internal.cannons.holders.CannonPositionHolder;
import at.pavlov.internal.container.CannonBlocks;
import at.pavlov.internal.container.holders.BlockHolder;
import at.pavlov.internal.container.holders.ItemHolder;
import at.pavlov.internal.container.holders.SoundHolder;
import at.pavlov.internal.container.location.CannonVector;
import at.pavlov.internal.container.location.Coordinate;
import at.pavlov.internal.projectile.Projectile;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Data public abstract class CannonDesign <
        Block,
        Facing,
        Material,
        IH extends ItemHolder<Material>,
        SH extends SoundHolder<?>,
        SB extends BlockHolder<Block>,
        CB extends CannonBlocks<SB>
        > {
	//general
	protected String designID;
	protected String designName;
    protected String messageName;
    protected String description;
    protected boolean lastUserBecomesOwner;
	
	//sign
	protected boolean signRequired;
	
	//ammunition_consumption
	protected String gunpowderName;
	protected IH gunpowderType;
    protected boolean gunpowderNeeded;
    protected boolean gunpowderConsumption;
    protected boolean projectileConsumption;
	protected boolean ammoInfiniteForPlayer;
    protected boolean ammoInfiniteForRedstone;
    protected boolean autoreloadRedstone;
	protected boolean removeChargeAfterFiring;
	protected boolean autoloadChargeWhenLoadingProjectile;
	protected boolean preloaded;

    //barrelProperties
	protected int maxLoadableGunpowder;
	protected double multiplierVelocity;
	protected double spreadOfCannon;
	
	//timings
	protected double blastConfusion;
	protected double fuseBurnTime;
	protected double fuseBurnTimeRandomness;
    protected double barrelCooldownTime;
	protected double loadTime;
	
    //angles
	protected Facing defaultHorizontalFacing;
	protected double defaultVerticalAngle;
	protected double maxHorizontalAngle;
	protected double minHorizontalAngle;
	protected double maxVerticalAngle;
	protected double minVerticalAngle;
    protected double maxHorizontalAngleOnShip;
    protected double minHorizontalAngleOnShip;
    protected double maxVerticalAngleOnShip;
    protected double minVerticalAngleOnShip;
	protected double angleStepSize;
	protected double angleLargeStepSize;
	protected int angleUpdateSpeed;
	protected boolean angleUpdateMessage;

    //impactPredictor
    protected boolean predictorEnabled;
    protected int predictorDelay;         //in ms
    protected int predictorUpdate;        //in ms

	//sentry
	protected boolean sentry;
	protected boolean sentryIndirectFire;
    protected int sentryMinRange;
    protected int sentryMaxRange;
	protected double sentrySpread;
	protected int sentryUpdateTime;				//in ms
    protected int sentrySwapTime;				    //in ms

	//linkCannons
	protected boolean linkCannonsEnabled;
	protected int linkCannonsDistance;

    //heatManagment
    protected boolean heatManagementEnabled;
    protected boolean automaticTemperatureControl;
    protected double burnDamage;
    protected double burnSlowing;
    protected double heatIncreasePerGunpowder;
    protected double coolingCoefficient;
    protected double coolingAmount;
    protected boolean automaticCooling;
    protected double warningTemperature;
    protected double criticalTemperature;
    protected double maximumTemperature;
    protected List<IH> itemCooling = new ArrayList<>();
    protected List<IH> itemCoolingUsed = new ArrayList<>();

    //Overloading stuff
    protected boolean overloadingEnabled;
    protected boolean overloadingRealMode;
    protected double overloadingExponent;
    protected double overloadingChanceInc;
    protected int overloadingMaxOverloadableGunpowder;
    protected double overloadingChanceOfExplosionPerGunpowder;
    protected boolean overloadingDependsOfTemperature;

	protected double economyBuildingCost;
	protected double economyDismantlingRefund;
	protected double economyDestructionRefund;

	//realisticBehaviour
	protected boolean FiringItemRequired;
    protected double sootPerGunpowder;
    protected int projectilePushing;
	protected boolean hasRecoil;
	protected boolean frontloader;
	protected boolean rotable;
    protected int massOfCannon;
    protected int startingSoot;
    protected double explodingLoadedCannons;
    protected boolean fireAfterLoading;
    protected double dismantlingDelay;
	
	//permissions
	protected String permissionBuild;
	protected String permissionDismantle;
    protected String permissionRename;
	protected String permissionLoad;
	protected String permissionFire;
	protected String permissionAdjust;
	protected String permissionAutoaim;
    protected String permissionObserver;
	protected String permissionTargetTracking;
	protected String permissionRedstone;
    protected String permissionThermometer;
    protected String permissionRamrod;
	protected String permissionAutoreload;
	protected String permissionSpreadMultiplier;
	
	//accessRestriction
	protected boolean accessForOwnerOnly;
	
	//allowedProjectile
	protected List<String> allowedProjectiles;

    //sounds
    protected SH soundCreate;
    protected SH soundDestroy;
	protected SH soundDismantle;
    protected SH soundAdjust;
    protected SH soundIgnite;
    protected SH soundFiring;
    protected SH soundGunpowderLoading;
    protected SH soundGunpowderOverloading;
    protected SH soundCool;
    protected SH soundHot;
    protected SH soundRamrodCleaning;
    protected SH soundRamrodCleaningDone;
    protected SH soundRamrodPushing;
    protected SH soundRamrodPushingDone;
    protected SH soundThermometer;
    protected SH soundEnableAimingMode;
    protected SH soundDisableAimingMode;
	protected SH soundSelected;

	
	//constructionblocks:
	protected Block schematicBlockTypeIgnore;     				//this block this is ignored in the schematic file
    protected Block schematicBlockTypeMuzzle;					//location of the muzzle
    protected Block schematicBlockTypeRotationCenter;			//location of the roatation
    protected Block schematicBlockTypeChestAndSign;				//locations of the chest and sign
    protected Block schematicBlockTypeRedstoneTorch;				//locations of the redstone torches
    protected Block schematicBlockTypeRedstoneWireAndRepeater;	//locations of the redstone wires and repeaters
    protected Block schematicBlockTypeRedstoneTrigger; 			//locations of button or levers
    protected Block ingameBlockTypeRedstoneTrigger;    			//block which is placed instead of the place holder
    protected Block schematicBlockTypeRightClickTrigger; 		//locations of the right click trigger
    protected Block ingameBlockTypeRightClickTrigger;   			//block type of the tigger in game
    protected Block schematicBlockTypeFiringIndicator;			//location of the firing indicator
    protected List<Block> schematicBlockTypeProtected;				//list of blocks that are protected from explosions (e.g. buttons)
    
    //cannon design block lists for every direction (NORTH, EAST, SOUTH, WEST)
    protected final HashMap<Facing, CB> cannonBlockMap = new HashMap<>();
	protected final HashSet<Material> allowedMaterials = new HashSet<>();


    
    /**
     * returns the rotation center of a cannon design
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> Coordinate getRotationCenter(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
            CannonVector vector = cannonBlocks.getRotationCenter().clone().add(cannon.getOffset());
    		return new Coordinate(vector, cannon.getWorld());
    	}

    	CannonLogger.getLogger().info("missing rotation center for cannon design " + cannon.getCannonName());
    	return new Coordinate(cannon.getOffset(), cannon.getWorld());
    }


    /**
     * returns the muzzle location
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> Coordinate getMuzzle(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null)
    	{
            CannonVector vector = cannonBlocks.getMuzzle().clone().add(cannon.getOffset());
    		return new Coordinate(vector, cannon.getWorld());
    	}

    	CannonLogger.getLogger().info("missing muzzle location for cannon design " + cannon.getCannonName());
    	return new Coordinate(cannon.getOffset(), cannon.getWorld());
    }
    
    /**
     * returns one trigger location
     * @param cannon the used cannon
     * @return the firing trigger of the cannon - can be null if the cannon has no trigger
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> Coordinate getFiringTrigger(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	if (cannonBlocks != null && cannonBlocks.getFiringTrigger() != null)
    	{
            CannonVector vector = cannonBlocks.getFiringTrigger().clone().add(cannon.getOffset());
            return new Coordinate(vector, cannon.getWorld());
    	}
    	return null;
    }
    
    /**
     * returns a list of all cannonBlocks
     * @param cannonDirection - the direction the cannon is facing
     * @return List of cannon blocks
     */
    public List<SB> getAllCannonBlocks(Facing cannonDirection)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannonDirection);
    	if (cannonBlocks != null)
    	{
    		return cannonBlocks.getAllCannonBlocks();
    	}
    	
    	return new ArrayList<>();
    }


    /**
     * returns a list of all cannonBlocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getAllCannonBlocks(T cannon)
    {
        CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Coordinate> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (SB block : cannonBlocks.getAllCannonBlocks())
            {
                CannonVector vect = block.toVector();
                CannonVector calc = vect.clone().add(cannon.getOffset());
                locList.add(new Coordinate(calc, cannon.getWorld()));
            }
        }
        return locList;
    }

    /**
     * returns a list of all destructible blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getDestructibleBlocks(T cannon)
    {
     	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getDestructibleBlocks())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all firingIndicator blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getFiringIndicator(T cannon)
    {
     	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getFiringIndicator())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all loading interface blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getLoadingInterface(T cannon)
    {
        CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Coordinate> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (CannonVector vect : cannonBlocks.getBarrelBlocks())
            {
                CannonVector vector = vect.clone().add(cannon.getOffset());
                locList.add(new Coordinate(vector, cannon.getWorld()));
            }
        }
        return locList;
    }

    /**
     * returns a list of all barrel blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getBarrelBlocks(T cannon)
    {
        CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
        List<Coordinate> locList = new ArrayList<>();
        if (cannonBlocks != null)
        {
            for (CannonVector vect : cannonBlocks.getBarrelBlocks())
            {
                CannonVector vector = vect.clone().add(cannon.getOffset());
                locList.add(new Coordinate(vector, cannon.getWorld()));
            }
        }
        return locList;
    }
    
    /**
     * returns a list of all right click trigger blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getRightClickTrigger(T cannon)
    {
     	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRightClickTrigger())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone trigger blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getRedstoneTrigger(T cannon)
    {
     	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRedstoneTrigger())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    
    /**
     * returns a list of all chest/sign blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getChestsAndSigns(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (SB block : cannonBlocks.getChestsAndSigns())
    		{
                var vector = block.toVector().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone torch blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getRedstoneTorches(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (CannonVector vect : cannonBlocks.getRedstoneTorches())
    		{
                CannonVector vector = vect.clone().add(cannon.getOffset());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns a list of all redstone wire/repeater blocks
     */
    public <T extends CannonPositionHolder<Facing> & CannonMainDataHolder> List<Coordinate> getRedstoneWireAndRepeater(T cannon)
    {
    	CB cannonBlocks  = cannonBlockMap.get(cannon.getCannonDirection());
    	List<Coordinate> locList = new ArrayList<>();
    	if (cannonBlocks != null)
    	{
    		for (SB block : cannonBlocks.getRedstoneWiresAndRepeater())
    		{
                var vector = cannon.getOffset().add(block.toVector());
    			locList.add(new Coordinate(vector, cannon.getWorld()));
    		}
    	}
		return locList;
    }
    
    /**
     * returns true if the projectile has the same Id of a allowed projectile
     * @param projectile projectile to load
     * @return true if the projectile can be loaded in this type of cannon
     */
    public <T extends Projectile<?,?,?,?,?,?,?,?,?>> boolean canLoad(T projectile)
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

	public boolean isAllowedMaterial(Material m) {
		return allowedMaterials.contains(m);
	}


    /*
     * is this Item a cooling tool to cool down a cannon
     * @param item - item to check
     * @return - true if this item is in the list of cooling items
     */
    /*
    public boolean isCoolingTool(ItemStack item)
    {
    	//todo rework tool properties
        for (BukkitItemHolder mat : itemCooling)
        {
            if (mat.equalsFuzzy(item))
                return true;
        }
        return false;
    }*/

    /*
     * returns the used used item. E.g. a water bucket will be an empty bucket.
     * @param item - the item used for the event
     * @return the new item which replaces the old one
     */
    /*
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
    }*/

    public abstract void putCannonBlockMap(Facing cannonDirection, CB blocks);
}
