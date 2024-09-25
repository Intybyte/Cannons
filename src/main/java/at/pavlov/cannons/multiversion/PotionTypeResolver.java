package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.Cannons;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;

public final class PotionTypeResolver {
    private static final int[] version = VersionHandler.getVersion();
    private static PotionEffectType slow;
    private static PotionEffectType nausea;

    static {
        initSlow();
        initNausea();
    }

    private PotionTypeResolver() {}

    public static void initSlow() {
        if (version[1] >= 20) {
            slow = PotionEffectType.SLOWNESS;
        } else {
            try {
                Field field = PotionEffectType.class.getDeclaredField("SLOW");
                field.setAccessible(true);
                slow = (PotionEffectType) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    public static void initNausea() {
        if (version[1] >= 20) {
            nausea = PotionEffectType.NAUSEA;
        } else if (version[2] <= 19) {
            try {
                Field field = PotionEffectType.class.getDeclaredField("CONFUSION");
                field.setAccessible(true);
                nausea = (PotionEffectType) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    public static PotionEffectType getSlow() {
        return slow;
    }

    public static PotionEffectType getNausea() {
        return nausea;
    }
}
