package at.pavlov.internal.container;

import at.pavlov.internal.CannonLogger;
import lombok.Data;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.MatchResult;

@Data
public abstract class SpawnMaterialHolder<Block> {
    protected Block material;
    protected int minAmount;
    protected int maxAmount;

    public SpawnMaterialHolder(String str) {
        // split string at space
        // id:data min-max
        // 10:0 1-2
        try {
            Scanner s = new Scanner(str);
            s.findInLine("(\\S+)\\s(\\d+)-(\\d+)");
            MatchResult result = s.match();
            setBlockString(result.group(1));
            setMinAmount(Integer.parseInt(result.group(2)));
            setMaxAmount(Integer.parseInt(result.group(3)));
            s.close();
        } catch (Exception e) {
            CannonLogger.getLogger().log(Level.SEVERE,"Error while converting " + str + ". Check formatting (minecraft:cobweb 1-2)");
            material = getDefaultBlock();
            setMinAmount(0);
            setMaxAmount(0);
        }
    }

    public SpawnMaterialHolder(Block material, int minAmount, int maxAmount) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public abstract void setBlockString(String str);
    public abstract Block getDefaultBlock();
}
