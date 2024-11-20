package at.pavlov.cannons.multiversion;

import at.pavlov.cannons.Cannons;
import org.bukkit.entity.EntityType;

import java.lang.reflect.Field;

public class EntityTypeResolver {
    private static final int[] version = VersionHandler.getVersion();
    private static EntityType firework;

    static {
        initFirework();
    }

    private EntityTypeResolver() {}

    private static void initFirework() {
        if (version[1] >= 20) {
            firework = EntityType.FIREWORK_ROCKET;
        } else {
            try {
                Field field = EntityType.class.getDeclaredField("FIREWORK");
                field.setAccessible(true);
                firework = (EntityType) field.get(null);
            } catch (Exception e) {
                Cannons.logger().severe("Version support not found");
            }
        }
    }

    public static EntityType getFirework() {
        return firework;
    }
}
