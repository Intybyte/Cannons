package at.pavlov.cannons.utils;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.CannonDesign;
import me.vaan.schematiclib.base.schematic.Schematic;
import org.bukkit.block.BlockFace;

import java.util.Comparator;
import java.util.logging.Level;

public class DesignComparator implements Comparator<CannonDesign>
{

	@Override
	public int compare(CannonDesign design1, CannonDesign design2)
	{
		int amount1 = getCannonBlockAmount(design1);
		int amount2 = getCannonBlockAmount(design2);
		
		return amount2 - amount1;
	}
	
	private Integer getCannonBlockAmount(CannonDesign design)
	{
		if (design == null) return 0;
		//if the design is invalid something goes wrong, message the user
        Schematic schematic = design.getSchematicMap().get(BlockFace.NORTH);
		if (schematic == null)
		{
			Cannons.logger().log(Level.SEVERE, "invalid cannon design for " + design.getDesignName());
			return 0;
		}
		
		return schematic.positions().size();
	}

}
