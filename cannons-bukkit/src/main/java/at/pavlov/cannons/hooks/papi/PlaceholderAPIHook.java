package at.pavlov.cannons.hooks.papi;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.internal.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class PlaceholderAPIHook extends BukkitHook<Void> {
    private boolean working = false;

    public PlaceholderAPIHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public Void hook() {
        return null;
    }

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return;
        }

        new CannonsPAPIExpansion(plugin).register();
        working = true;
        plugin.logInfo(ChatColor.GREEN + enabledMessage());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean active() {
        return working;
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return PlaceholderAPIHook.class;
    }
}
