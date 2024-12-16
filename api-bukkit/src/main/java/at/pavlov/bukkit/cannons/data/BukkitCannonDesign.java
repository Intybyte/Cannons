package at.pavlov.bukkit.cannons.data;

import at.pavlov.bukkit.cannons.CannonBukkit;
import at.pavlov.bukkit.container.BukkitBlock;
import at.pavlov.bukkit.container.BukkitCannonBlocks;
import at.pavlov.bukkit.container.BukkitItemHolder;
import at.pavlov.bukkit.container.BukkitSoundHolder;
import at.pavlov.bukkit.factory.VectorUtils;
import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.cannons.data.CannonDesign;
import at.pavlov.internal.container.location.CannonVector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

public class BukkitCannonDesign extends CannonDesign<
        BlockData,
        BlockFace,
        Material,
        BukkitItemHolder,
        BukkitSoundHolder,
        BukkitBlock,
        BukkitCannonBlocks
        > {

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
