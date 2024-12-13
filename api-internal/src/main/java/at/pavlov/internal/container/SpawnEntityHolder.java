package at.pavlov.internal.container;

import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.enums.EntityDataType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Data abstract public class SpawnEntityHolder<Entity, Potion> {
    protected Entity type;
    protected int minAmount;
    protected int maxAmount;
    protected Map<EntityDataType, String> data;
    protected List<Potion> potionEffects;

    public SpawnEntityHolder(Entity type, int minAmount, int maxAmount, Map<EntityDataType, String> data) {
        this.type = type;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.data = data;
    }

    public SpawnEntityHolder(String str) {
        data = new HashMap<>();
        potionEffects = new ArrayList<>();

        try {
            // Split the input string into parts
            String[] parts = str.split(" ", 3);
            if (parts.length < 2) {
                throw new IllegalArgumentException("Input string must contain at least entity type and min-max values.");
            }

            // Parse entity type and min-max values
            parseEntityType(parts[0]);
            parseMinMax(parts[1]);

            // Parse additional entity data if present
            if (parts.length > 2) {
                parseEntityData(parts[2]);
            } else {
                logWarning("No additional data provided: ", str);
            }

        } catch (Exception e) {
            logError("Error parsing input", e, str);
            resetEntityData();
        }
    }

    public void resetEntityData() {
        setType(null);
        setMinAmount(0);
        setMaxAmount(0);
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
                } else if(EntityDataType.has(key)) {
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
            throw new IllegalArgumentException("Invalid min-max format: " + minMaxStr);
        }
        setMinAmount(Math.max(0, Integer.parseInt(range[0])));
        setMaxAmount(Math.min(100, Integer.parseInt(range[1])));
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

    public abstract void createPotionEffect(JsonObject attributes);
    public abstract void parseEntityType(String entityTypeStr);

    protected void logError(String message, Exception e, String context) {
        CannonLogger.getLogger().log(Level.SEVERE, message + (context != null ? ": " + context : ""), e);
    }

    protected void logWarning(String message, String context) {
        CannonLogger.getLogger().log(Level.WARNING, message + (context != null ? ": " + context : ""));
    }
}
