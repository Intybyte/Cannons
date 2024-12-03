package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.cannons.hooks.movecraft.listener.CraftDetectListener;
import at.pavlov.cannons.hooks.movecraft.listener.RotationListener;
import at.pavlov.cannons.hooks.movecraft.listener.TranslationListener;
import at.pavlov.cannons.hooks.movecraft.type.MaxCannonsProperty;
import at.pavlov.internal.Hook;
import net.countercraft.movecraft.Movecraft;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class MovecraftHook extends BukkitHook<Movecraft> {
    public MovecraftHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        if (!plugin.getMyConfig().isMovecraftEnabled()) {
            return;
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        Plugin movecraftPlugin = pluginManager.getPlugin("Movecraft");
        if (movecraftPlugin == null || !movecraftPlugin.isEnabled()) {
            plugin.logDebug("Movecraft not found or disabled");
            return;
        }

        if (!(movecraftPlugin instanceof Movecraft movecraft)) {
            plugin.logDebug("Movecraft plugin isn't the one expected");
            return;
        }

        hook = movecraft;

        MaxCannonsProperty.register();
        pluginManager.registerEvents(new CraftDetectListener(), plugin);
        pluginManager.registerEvents(new TranslationListener(), plugin);
        pluginManager.registerEvents(new RotationListener(), plugin);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(new CraftDetectListener());
        HandlerList.unregisterAll(new TranslationListener());
        HandlerList.unregisterAll(new RotationListener());
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return MovecraftHook.class;
    }
}
