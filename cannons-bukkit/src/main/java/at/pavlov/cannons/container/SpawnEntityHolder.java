package at.pavlov.cannons.container;

import at.pavlov.cannons.Enum.EntityDataType;
import at.pavlov.internal.MaxMinRandom;
import com.cryptomorin.xseries.XEntityType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data public class SpawnEntityHolder implements MaxMinRandom {
    private EntityType type;
    private int minAmount;
    private int maxAmount;
    private Map<EntityDataType, String> data;
    private List<PotionEffect> potionEffects;

    public SpawnEntityHolder(String str) {
        data = new HashMap<>();
        potionEffects = new ArrayList<>();

        try {
            // Split the input string into parts
            String[] parts = str.split(" ", 3);
            if (parts.length < 2) {
                Bukkit.getLogger().severe("Input string must contain at least entity type and min-max values.");
            }

            // Parse entity type and min-max values
            parseEntityType(parts[0]);
            parseMinMax(parts[1]);

            // Parse additional entity data if present
            if (parts.length > 2) {
                parseEntityData(parts[2]);
            } else {
                Bukkit.getLogger().warning("No additional data provided: " + str);
            }

        } catch (Exception e) {
            Bukkit.getLogger().severe("Error parsing input: " + str);
            resetEntityData();
        }
    }

    public SpawnEntityHolder(EntityType type, int minAmount, int maxAmount, Map<EntityDataType, String> data) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.data = data;
    }

    // TODO make a json utils
    public static PotionEffectType getPotionType(JsonElement element) {
        try {
            int id = getNumber(element);
            return PotionEffectType.getById(id);
        } catch (NumberFormatException e) {
            String str = element.getAsString();
            if (str == null) {
                Bukkit.getLogger().severe("Effect type string cannot be null");
                return null;
            }

            NamespacedKey potionKey = NamespacedKey.fromString(str);
            if (potionKey == null) {
                Bukkit.getLogger().severe("Effect type key not found, format should be something like: minecraft:strength");
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

    public void resetEntityData() {
        this.type = null;
        this.minAmount = 0;
        this.maxAmount = 0;
    }

    public void parseEntityData(String dataStr) {
        try {
            // Parse the data string as JSON
            JsonObject jsonObject = JsonParser.parseString(dataStr).getAsJsonObject();

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if (key.equalsIgnoreCase("effects")) {
                    parsePotionEffects(value);
                } else if (EntityDataType.has(key)) {
                    addEntityData(key, value.getAsString());
                } else {
                    throw new Exception("Invalid key: " + key);
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid entity data format: " + dataStr, e);
        }
    }

    public void parseMinMax(String minMaxStr) {
        String[] range = minMaxStr.split("-");
        if (range.length != 2) {
            Bukkit.getLogger().severe("Invalid min-max format: " + minMaxStr);
            minAmount = 0;
            maxAmount = 100;
        }

        try {
            minAmount = Math.max(0, Integer.parseInt(range[0]));
        } catch (Exception e) {
            Bukkit.getLogger().severe("Invalid min format: " + range[0] + " must be an integer");
            return;
        }

        try {
            maxAmount = Math.min(100, Integer.parseInt(range[1]));
        } catch (Exception e) {
            Bukkit.getLogger().severe("Invalid max format: " + range[1] + " must be an integer");
        }
    }

    public void addEntityData(String key, String value) {
        EntityDataType dataType = EntityDataType.lookup(key);
        if (dataType == null) {
            throw new IllegalArgumentException("Unsupported entity data key: " + key);
        }

        data.put(dataType, value);
    }

    public void parsePotionEffects(JsonElement effectsElement) {
        if (!effectsElement.isJsonArray()) {
            throw new IllegalArgumentException("Effects must be a JSON array.");
        }

        effectsElement.getAsJsonArray().forEach(effectElement -> {
            if (!effectElement.isJsonObject()) {
                throw new IllegalArgumentException("Each effect must be a JSON object.");
            }

            JsonObject effectObject = effectElement.getAsJsonObject();
            createPotionEffect(effectObject);
        });
    }

    public void parseEntityType(String entityTypeStr) {
        Optional<XEntityType> type = XEntityType.of(entityTypeStr);
        if (type.isEmpty()) {
            Bukkit.getLogger().severe("Invalid entity type: " + entityTypeStr + ", using snowball instead");
            this.type = XEntityType.SNOWBALL.get();
            return;
        }

        this.type = type.get().get();
    }

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
            Bukkit.getLogger().severe("Invalid potion effect attributes: " + attributes.toString());
            e.printStackTrace();
        }
    }
}
