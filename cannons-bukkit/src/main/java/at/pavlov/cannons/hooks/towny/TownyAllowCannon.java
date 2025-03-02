package at.pavlov.cannons.hooks.towny;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public enum TownyAllowCannon {
    ALL,
    TOWN,
    ALLIES;

    public static TownyAllowCannon fromConfig(FileConfiguration config, String key) {
        String result = config.getString(key, "TOWN");

        try {
            return TownyAllowCannon.valueOf(result.toUpperCase(Locale.ROOT));
        } catch (Exception ignored) {
            return TOWN;
        }
    }
}
