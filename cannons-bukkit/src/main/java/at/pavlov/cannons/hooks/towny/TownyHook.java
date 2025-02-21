package at.pavlov.cannons.hooks.towny;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.internal.Hook;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownBlockTypeHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.PluginManager;

public class TownyHook extends BukkitHook<TownyAPI> {
    @Getter
    private static TownBlockType artilleryType;

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
        artilleryType = new TownBlockType("Artillery Position");
        TownBlockTypeHandler.registerType(artilleryType);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return TownyHook.class;
    }
}
