package at.pavlov.bukkit.container.holders;

import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.container.holders.SpawnEntityHolder;
import at.pavlov.internal.enums.EntityDataType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class BukkitEntityHolder extends SpawnEntityHolder<EntityType, PotionEffect> {

    public BukkitEntityHolder(String str) {
        super(str);
    }

    public BukkitEntityHolder(EntityType type, int minAmount, int maxAmount, Map<EntityDataType, String> data) {
        super(type, minAmount, maxAmount, data);
    }

    @Override
    public void parseEntityType(String entityTypeStr) {
        try {
            setType(EntityType.valueOf(entityTypeStr));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid entity type: " + entityTypeStr, e);
        }
    }

    @Override
    public void createPotionEffect(JsonObject attributes) {
        try {
            PotionEffectType type = getPotionType(attributes.get("Id"));
            int duration = getNumber(attributes.get("Duration"));
            int amplifier = getNumber(attributes.get("Amplifier"));
            boolean ambient = getBoolean(attributes, "Ambient", false);
            boolean particles = getBoolean(attributes, "ShowParticles", false);
            boolean icon = getBoolean(attributes, "Icon", true);

            if (type != null && duration > 0 && amplifier > 0) {
                potionEffects.add(new PotionEffect(type, duration, amplifier, ambient, particles, icon));
            }
        } catch (Exception e) {
            logError("Invalid potion effect attributes", e, attributes.toString());
        }
    }

    // TODO make a json utils
    public static PotionEffectType getPotionType(JsonElement element) {
        try {
            int id = getNumber(element);
            return PotionEffectType.getById(id);
        } catch (NumberFormatException e) {
            String str = element.getAsString();
            if (str == null) {
                CannonLogger.getLogger().severe("Effect type string cannot be null");
                return null;
            }

            NamespacedKey potionKey = NamespacedKey.fromString(str);
            if (potionKey == null) {
                CannonLogger.getLogger().severe("Effect type key not found, format should be something like: minecraft:strength");
                return null;
            }

            return Registry.EFFECT.get(potionKey);
        }
    }

    public static int getNumber(JsonElement element) throws NumberFormatException {
        var str = element.getAsString();
        if (str.endsWith("b")) {
            return Integer.parseInt(str.substring(0, str.length() - 1));
        }

        return Integer.parseInt(str);
    }

    public static boolean getBoolean(JsonObject obj, String key, boolean default_value) {
        if (!obj.has(key)) {
            return default_value;
        }

        return getNumber(obj.get(key)) == 1;
    }
}
