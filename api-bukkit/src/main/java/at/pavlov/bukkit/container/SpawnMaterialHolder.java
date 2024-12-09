package at.pavlov.bukkit.container;

import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.Scanner;
import java.util.regex.MatchResult;

@Data
public class SpawnMaterialHolder {
    private BlockData material;
    private int minAmount;
    private int maxAmount;

    public SpawnMaterialHolder(String str) {
        //split string at space
        // id:data min-max
        // 10:0 1-2
        try {
            Scanner s = new Scanner(str);
            s.findInLine("(\\S+)\\s(\\d+)-(\\d+)");
            MatchResult result = s.match();
            material = Bukkit.createBlockData(result.group(1));
            setMinAmount(Integer.parseInt(result.group(2)));
            setMaxAmount(Integer.parseInt(result.group(3)));
            s.close();
        } catch (Exception e) {
            //Cannons.logger().log(Level.SEVERE,"Error while converting " + str + ". Check formatting (minecraft:cobweb 1-2)");
            material = Bukkit.createBlockData(Material.AIR);
            setMinAmount(0);
            setMaxAmount(0);
        }
    }

    public SpawnMaterialHolder(BlockData material, int minAmount, int maxAmount) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
