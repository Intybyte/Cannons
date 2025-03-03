package at.pavlov.cannons.hooks;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends BukkitHook<Economy> {

    public VaultHook(Cannons plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        if (plugin.getMyConfig().isEconomyDisabled()) {
            hook = null;
            return;
        }

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            hook = null;
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            hook = null;
            return;
        }

        hook = rsp.getProvider();
        plugin.logInfo(ChatColor.GREEN  + enabledMessage());
    }

    @Override
    public Class<? extends Hook<?>> getTypeClass() {
        return VaultHook.class;
    }
}
