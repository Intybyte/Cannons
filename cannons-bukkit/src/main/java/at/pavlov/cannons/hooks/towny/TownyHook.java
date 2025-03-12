package at.pavlov.cannons.hooks.towny;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.internal.Hook;
import com.palmergames.bukkit.towny.TownyAPI;
import lombok.SneakyThrows;
import org.bukkit.plugin.PluginManager;

public class TownyHook extends BukkitHook<TownyAPI> {

    public TownyHook(Cannons plugin) {
        super(plugin);
    }

    @SneakyThrows
    @Override
    public void onEnable() {

        if (!plugin.getMyConfig().isTownyEnabled()) {
            return;
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (!pluginManager.isPluginEnabled("Towny")) {
            plugin.logDebug("Towny not found or disabled");
            return;
        }

        hook = TownyAPI.getInstance();
        pluginManager.registerEvents(new TownyListeners(), plugin);
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return TownyHook.class;
    }
}
