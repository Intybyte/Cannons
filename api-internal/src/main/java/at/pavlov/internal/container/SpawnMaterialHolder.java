package at.pavlov.internal.container;

import at.pavlov.internal.CLogger;
import at.pavlov.internal.Key;
import at.pavlov.internal.MaxMinRandom;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.MatchResult;

@Setter
@Getter
public class SpawnMaterialHolder implements MaxMinRandom {
    private Key material;
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
            material = Key.from(result.group(1));
            setMinAmount(Integer.parseInt(result.group(2)));
            setMaxAmount(Integer.parseInt(result.group(3)));
            s.close();
        } catch (Exception e) {
            CLogger.logger.log(Level.SEVERE, "Error while converting " + str + ". Check formatting (minecraft:cobweb 1-2)");
            material = Key.mc("air");
            this.minAmount = 0;
            this.maxAmount = 0;
        }
    }

    public SpawnMaterialHolder(Key material, int minAmount, int maxAmount) {
        this.material = material;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }
}
