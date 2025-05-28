package at.pavlov.cannons.utils;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.container.ItemHolder;
import at.pavlov.cannons.container.SpawnEntityHolder;
import at.pavlov.internal.container.SpawnMaterialHolder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class ParseUtils {
    /**
     * converts a string to float
     * @param str string to convert
     * @return returns parsed number or default
     */
    public static float parseFloat(String str, float default_value) {
        if (str == null) {
            return default_value;
        }
        try {
            return Float.parseFloat(str);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    /**
     * converts a string to int
     * @param str string to convert
     * @return returns parsed number or default
     */
    public static int parseInt(String str, int default_value) {
        if (str == null) {
            return default_value;
        }
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    /**
     * converts a string to color
     * @param str string to convert
     * @return returns parsed color or default
     */
    public static Color parseColor(String str, Color default_value) {
        if (str == null || str.isEmpty()) {
            return default_value;
        }

        try {
            return Color.fromRGB(Integer.parseInt(str));
        } catch (Exception e) {
            throw new NumberFormatException();
        }
    }

    /**
     * converts a string to Potion effect
     * @param str string to convert
     * @return returns parsed number or default
     */
    public static PotionData parsePotionData(String str, PotionData default_value) {
        if (str == null) {
            return default_value;
        }
        str = str.toLowerCase();
        for (PotionType pt : PotionType.values()) {
            if (!str.contains(pt.toString().toLowerCase())) {
                continue;
            }

            boolean extended = str.contains("long");
            boolean upgraded = str.contains("strong");
            Cannons.logSDebug("Potion parsing: " + str);
            return new PotionData(pt, extended, upgraded);
        }
        return default_value;
    }

    /**
     * converts a string to float
     * @param str string to convert
     * @return returns parsed number or default
     */
    public static Particle parseParticle(String str, Particle default_value) {
        if (str == null || str.isEmpty()) {
            return default_value;
        }

        for (Particle pt : Particle.values())
            if (str.equalsIgnoreCase(pt.toString())){
                return pt;
            }
        return default_value;
    }

    /**
     * converts a string to Itemstack
     * @param str string to convert
     * @return returns parsed number or default
     */
    public static ItemStack parseItemstack(String str, ItemStack default_value) {
        if (str == null) {
            return default_value;
        }

        for (Material mt : Material.values())
            if (str.equalsIgnoreCase(mt.toString())){
                return new ItemStack(mt);
            }
        return default_value;
    }

    /**
     * returns a list of Material
     * @param stringList list of Materials as strings
     * @return list of MaterialHolders
     */
    public static List<BlockData> toBlockDataList(List<String> stringList) {
        List<BlockData> blockDataList = new ArrayList<>();

        for (String str : stringList) {
            BlockData material = Bukkit.createBlockData(str);
            blockDataList.add(material);
        }

        return blockDataList;
    }

    /**
	 * returns a list of ItemHolder
	 * @param stringList list of Materials as strings
	 * @return list of ItemHolders
	 */
	public static List<ItemHolder> toItemHolderList(List<String> stringList) {
		List<ItemHolder> materialList = new ArrayList<>();

		for (String str : stringList) {
            ItemHolder material = new ItemHolder(str);
			//if id == -1 the str was invalid
            materialList.add(material);
		}

		return materialList;
	}

    /**
     * returns a list of ItemHolder. Formatting id:data min:max
     * @param stringList list of strings to convert
     * @return list of converted SpawnItemHolder
     */
    public static List<SpawnMaterialHolder> toSpawnMaterialHolderList(List<String> stringList) {
        List<SpawnMaterialHolder> materialList = new ArrayList<SpawnMaterialHolder>();
        for (String str : stringList) {
            SpawnMaterialHolder material = new SpawnMaterialHolder(str);
            materialList.add(material);
        }

        return materialList;
    }

    /**
     * returns a list of MaterialHolder. Formatting id:data min:max
     * @param stringList list of strings to convert
     * @return list of converted SpawnMaterialHolder
     */
    public static List<SpawnEntityHolder> toSpawnEntityHolderList(List<String> stringList) {
        List<SpawnEntityHolder> entityList = new ArrayList<>();

        for (String str : stringList) {
            SpawnEntityHolder entity = new SpawnEntityHolder(str);
            //if id == -1 the str was invalid
            if (entity.getType() != null)
                entityList.add(entity);
        }

        return entityList;
    }
}
