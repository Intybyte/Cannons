package at.pavlov.cannons.exchange;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;

public class ExchangeLoader {

    public static BExchanger of(String type, FileConfiguration config, String key, Object defaultValue) {
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "vault" -> {
                double amount = config.getDouble(key, (Double) defaultValue);
                yield new VaultExchanger(amount);
            }
            default -> new EmptyExchanger();
        };
    }
}
