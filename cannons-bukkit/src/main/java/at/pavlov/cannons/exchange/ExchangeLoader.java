package at.pavlov.cannons.exchange;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.Exchanger;
import at.pavlov.internal.Key;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

public class ExchangeLoader {
    @FunctionalInterface
    public interface Supplier {
        BExchanger supply(FileConfiguration config, String key, Exchanger.Type type);
    }

    private static final HashMap<Key, ExchangeLoader.Supplier> registry = new HashMap<>();

    public static BExchanger of(String type, FileConfiguration config, String key, Exchanger.Type typeExchange) {
        return of(Key.from(type), config, key, typeExchange);
    }

    public static BExchanger of(Key type, FileConfiguration config, String key, Exchanger.Type typeExchange) {
        var value = registry.get(type);
        if (value == null) {
            Bukkit.getLogger().severe(type + " ExchangeLoader not found. Returning empty exchanger as a fallback.");
            return new EmptyExchanger();
        }

        return value.supply(config, key, typeExchange);
    }

    public static boolean canRegister(Key key) {
        return !registry.containsKey(key);
    }

    // You will probably need to load registry entries in the onLoad part of your plugin
    public static void register(Key key, Supplier supplier) {
        if (registry.containsKey(key)) {
            throw new RuntimeException(key + " for ExchangeLoader is already taken.");
        }

        registry.put(key, supplier);
    }

    @ApiStatus.Internal
    public static void registerDefaults() {
        //Cannons.getPlugin().getHookManager().
        register(Key.cannons("vault"), (config, key, typeExchange) -> {
            double amount = config.getDouble(key, 0.0);
            return new VaultExchanger(amount, typeExchange);
        });

        register(Key.cannons("empty"), (config, key, typeExchange) -> new EmptyExchanger());
    }
}
