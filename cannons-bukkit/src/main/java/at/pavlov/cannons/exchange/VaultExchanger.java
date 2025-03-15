package at.pavlov.cannons.exchange;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.hooks.VaultHook;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public record VaultExchanger(double money, Type type) implements BExchanger {
    private static final Economy economy;
    static {
        Economy tmpVar = null;
        try {
            var hook = Cannons
                    .getPlugin()
                    .getHookManager()
                    .getHook(VaultHook.class);
            if (hook != null) {
                tmpVar = hook.hook();
            }
        } catch (Exception ignored) {}

        economy = tmpVar;
    }


    @Override
    public boolean execute(OfflinePlayer player, Cannon cannon) {
        if (economy == null) {
            return false;
        }

        if (type == Type.DEPOSIT) {
            economy.depositPlayer(player, money);
            return true;
        }

        if (type == Type.WITHDRAW) {
            EconomyResponse response = economy.withdrawPlayer(player, money);
            return response.transactionSuccess();
        }

        throw new UnsupportedOperationException("Define operation type.");
    }

    @Override
    public @NotNull String successMessage() {
        return economy.format(money);
    }
}
