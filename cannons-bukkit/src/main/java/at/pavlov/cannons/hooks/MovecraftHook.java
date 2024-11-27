package at.pavlov.cannons.hooks;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.movecraft.listener.CraftDetectListener;
import at.pavlov.cannons.movecraft.listener.ProjectileImpactListener;
import at.pavlov.cannons.movecraft.listener.RotationListener;
import at.pavlov.cannons.movecraft.listener.TranslationListener;
import at.pavlov.internal.Hook;
import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.combat.MovecraftCombat;
import org.bukkit.plugin.Plugin;

public class MovecraftHook extends BukkitHook<Movecraft> {
    public MovecraftHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        Plugin movecraftPlugin = plugin.getServer().getPluginManager().getPlugin("Movecraft");
        if (movecraftPlugin == null || !movecraftPlugin.isEnabled()) {
            plugin.logDebug("Movecraft not found or disabled");
            return;
        }

        if (!(movecraftPlugin instanceof Movecraft movecraft)) {
            plugin.logDebug("Movecraft plugin isn't the one expected");
            return;
        }

        hook = movecraft;

        var pluginManager = plugin.getServer().getPluginManager();
        if (plugin.getMyConfig().isMovecraftDamageTracking()) {
            // Load Movecraft-Combat plugin
            Plugin mcc = plugin.getServer().getPluginManager().getPlugin("Movecraft-Combat");
            if (mcc instanceof MovecraftCombat) {
                plugin.logInfo("Movecraft-Combat found");
                pluginManager.registerEvents(new ProjectileImpactListener(), plugin);
            } else {
                plugin.logInfo("Movecraft-Combat plugin not found!");
            }
        }


        pluginManager.registerEvents(new CraftDetectListener(), plugin);
        pluginManager.registerEvents(new TranslationListener(), plugin);
        pluginManager.registerEvents(new RotationListener(), plugin);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public Class<? extends Hook> getTypeClass() {
        return MovecraftHook.class;
    }
}
