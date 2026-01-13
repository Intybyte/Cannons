package at.pavlov.cannons.hooks.movecraft.type.properties;

import at.pavlov.cannons.Cannons;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.property.IntegerProperty;
import org.bukkit.NamespacedKey;

import java.util.function.Function;

public class PropertyWrapperInt {

    public final NamespacedKey key;
    public final Function<CraftType, Integer> defaultProvider;
    public final String fileEntryName;

    public PropertyWrapperInt(NamespacedKey key, Function<CraftType, Integer> defaultProvider) {
        this.key = key;
        this.defaultProvider = defaultProvider;
        this.fileEntryName = PropertyUtils.snakeToCamel(key.getKey());
    }

    public void register() {
        String configName = PropertyUtils.snakeToCamel(key.getKey());
        if (defaultProvider == null) {
            CraftType.registerProperty(new IntegerProperty(configName, key));
        } else {
            CraftType.registerProperty(new IntegerProperty(configName, key, defaultProvider));
        }
    }

    public Integer get(CraftType type) {
        try {
            // movecraft treats it as Integer but returns a not null int primitive... whatever
            return type.getIntProperty(key);
        } catch (Exception exception) {
            if (defaultProvider != null) return defaultProvider.apply(type);

            PropertyWrapper.notifyError.add(type);
            Cannons.getPlugin().logSevere(
                "Failed to get " + fileEntryName + " property from craft " +
                    type.getStringProperty(CraftType.NAME) +
                    " so it won't be applied. - Cause: " +
                    exception.getMessage()
            );

            return null;
        }
    }
}
