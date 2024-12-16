package at.pavlov.cannons.utils;

import at.pavlov.bukkit.cannons.data.BukkitCannonDesign;
import at.pavlov.cannons.Cannons;
import org.bukkit.block.BlockFace;

import java.util.Comparator;
import java.util.logging.Level;

public class DesignComparator implements Comparator<BukkitCannonDesign>
{

	@Override
	public int compare(BukkitCannonDesign design1, BukkitCannonDesign design2)
	{
		int amount1 = getCannonBlockAmount(design1);
		int amount2 = getCannonBlockAmount(design2);
		
		return amount2 - amount1;
	}
	
	private Integer getCannonBlockAmount(BukkitCannonDesign design)
	{
		if (design == null) return 0;
		//if the design is invalid something goes wrong, message the user
		if (design.getAllCannonBlocks(BlockFace.NORTH) == null) 
		{
			Cannons.logger().log(Level.SEVERE, "invalid cannon design for " + design.getDesignName());
			return 0;
		}
		
		return design.getAllCannonBlocks(BlockFace.NORTH).size();
	}

}
