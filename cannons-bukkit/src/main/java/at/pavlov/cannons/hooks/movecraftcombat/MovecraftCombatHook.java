package at.pavlov.cannons.hooks.movecraftcombat;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.hooks.BukkitHook;
import at.pavlov.internal.Hook;
import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;

public class MovecraftCombatHook extends BukkitHook<MovecraftCombat> {
    public MovecraftCombatHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        if (!plugin.getMyConfig().isMovecraftCannonEnabled()) {
            return;
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();
        if (!pluginManager.isPluginEnabled("Movecraft-Combat") || !pluginManager.isPluginEnabled("Movecraft")) {
            return;
        }

        hook = MovecraftCombat.getInstance();
        pluginManager.registerEvents(new ProjectileImpactListener(), plugin);
        pluginManager.registerEvents(new FireFallingListener(), plugin);
        plugin.logInfo(ChatColor.GREEN + enabledMessage());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(new ProjectileImpactListener());
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return MovecraftCombatHook.class;
    }
}
