package at.pavlov.cannons.hooks.movecraft;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.cannons.hooks.movecraft.listener.CraftReleaseListener;
import at.pavlov.cannons.hooks.movecraft.listener.PreventRecreateHandler;
import at.pavlov.cannons.hooks.movecraft.listener.CraftDetectListener;
import at.pavlov.cannons.hooks.movecraft.listener.ReleaseListener;
import at.pavlov.cannons.hooks.movecraft.listener.RotationListener;
import at.pavlov.cannons.hooks.movecraft.listener.SinkListener;
import at.pavlov.cannons.hooks.movecraft.listener.TranslationListener;
import at.pavlov.internal.Hook;
import net.countercraft.movecraft.Movecraft;
import org.bukkit.ChatColor;
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

        pluginManager.registerEvents(new CraftDetectListener(), plugin);
        pluginManager.registerEvents(new TranslationListener(), plugin);
        pluginManager.registerEvents(new RotationListener(), plugin);
        pluginManager.registerEvents(new SinkListener(), plugin);
        pluginManager.registerEvents(new PreventRecreateHandler(), plugin);
        pluginManager.registerEvents(new CraftReleaseListener(), plugin);
        pluginManager.registerEvents(new ReleaseListener(), plugin);
        plugin.logInfo(ChatColor.GREEN + enabledMessage());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return MovecraftHook.class;
    }
}
