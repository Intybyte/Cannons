package at.pavlov.cannons.hooks.movecraft.type.properties;

import at.pavlov.cannons.Cannons;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.property.BooleanProperty;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class PropertyWrapperBoolean {
    public final Set<CraftType> notifyError = new HashSet<>();

    public final NamespacedKey key;
    public final Function<CraftType, Boolean> defaultProvider;
    public final String fileEntryName;

    public PropertyWrapperBoolean(NamespacedKey key, Function<CraftType, Boolean> defaultProvider) {
        this.key = key;
        this.defaultProvider = defaultProvider;
        this.fileEntryName = PropertyUtils.snakeToCamel(key.getKey());
    }

    public void register() {
        String configName = PropertyUtils.snakeToCamel(key.getKey());
        if (defaultProvider == null) {
            CraftType.registerProperty(new BooleanProperty(configName, key));
        } else {
            CraftType.registerProperty(new BooleanProperty(configName, key, defaultProvider));
        }
    }

    public @Nullable Boolean get(CraftType type) {
        if (notifyError.contains(type)) return null;

        try {
            return type.getBoolProperty(key);
        } catch (Exception exception) {
            if (defaultProvider != null) return defaultProvider.apply(type);

            notifyError.add(type);
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

