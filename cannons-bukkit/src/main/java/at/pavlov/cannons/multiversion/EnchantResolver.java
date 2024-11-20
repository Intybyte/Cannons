package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.Cannons;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.Field;

public class EnchantResolver {
    private static final int[] version = VersionHandler.getVersion();
    private static Enchantment projectile_protection;
    private static Enchantment blast_protection;
    private static Enchantment protection;
    private static Enchantment unbreaking;

    static {
        initProtection();
        initProjectileProt();
        initBlastProt();
        initUnbreaking();
    }

    private EnchantResolver() {}

    private static void initUnbreaking() {
        if (version[1] >= 20) {
            unbreaking = Enchantment.UNBREAKING;
        } else {
            try {
                Field field = Enchantment.class.getDeclaredField("DURABILITY");
                field.setAccessible(true);
                unbreaking = (Enchantment) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    private static void initBlastProt() {
        if (version[1] >= 20) {
            blast_protection = Enchantment.BLAST_PROTECTION;
        } else {
            try {
                Field field = Enchantment.class.getDeclaredField("PROTECTION_EXPLOSIONS");
                field.setAccessible(true);
                blast_protection = (Enchantment) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    private static void initProjectileProt() {
        if (version[1] >= 20) {
            projectile_protection = Enchantment.PROJECTILE_PROTECTION;
        } else {
            try {
                Field field = Enchantment.class.getDeclaredField("PROTECTION_PROJECTILE");
                field.setAccessible(true);
                projectile_protection = (Enchantment) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    private static void initProtection() {
        if (version[1] >= 20) {
            protection = Enchantment.PROTECTION;
        } else {
            try {
                Field field = Enchantment.class.getDeclaredField("PROTECTION_ENVIRONMENTAL");
                field.setAccessible(true);
                protection = (Enchantment) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    public static Enchantment getProjectileProtection() {
        return projectile_protection;
    }

    public static Enchantment getBlastProtection() {
        return blast_protection;
    }

    public static Enchantment getProtection() {
        return protection;
    }

    public static Enchantment getUnbreaking() {
        return unbreaking;
    }
}
