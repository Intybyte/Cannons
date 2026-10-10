package at.pavlov.cannons.hooks.windfarer;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.cannons.hooks.windfarer.listener.*;
import at.pavlov.internal.Hook;
import net.countercraft.movecraft.Movecraft;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class WindfarerHook extends BukkitHook<Movecraft> {

    public WindfarerHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        if (!plugin.getMyConfig().isWindfarerEnabled()) {
            return;
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        Plugin windfarerPlugin = pluginManager.getPlugin("Windfarer");
        // TTE: Windfarer provides movecraft, thus, if Windfarer is found as well, the movecraft hook must not be enabled!
        if (windfarerPlugin == null || !windfarerPlugin.isEnabled()) {
            plugin.logDebug("Windfarer not found or disabled");
            return;
        }

        if (!(windfarerPlugin instanceof Movecraft windfarer)) {
            plugin.logDebug("Windfarer plugin isn't the one expected");
            return;
        }

        hook = windfarer;

        pluginManager.registerEvents(new CraftDetectListener(), plugin);
        pluginManager.registerEvents(new PostMovementListener(), plugin);
        pluginManager.registerEvents(new RegisterDetectionStepsListener(), plugin);
        pluginManager.registerEvents(new ReleaseListener(), plugin);
        pluginManager.registerEvents(new SinkListener(), plugin);

        plugin.logInfo(ChatColor.GREEN + enabledMessage());
    }

    @Override
    public void onDisable() {
        // Unnecessary, left empty for now. Can be used in the future to un-register event handlers or similar
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return WindfarerHook.class;
    }
}
