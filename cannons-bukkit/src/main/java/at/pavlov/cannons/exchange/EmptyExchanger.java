package at.pavlov.cannons.exchange;

import at.pavlov.cannons.cannon.Cannon;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class EmptyExchanger implements BExchanger {

    @Override
    public boolean execute(OfflinePlayer player, Cannon cannon) {
        return true;
    }

    @Override
    public @NotNull String formatted() {
        return "Nothing";
    }

    @Override
    public @NotNull Type type() {
        return Type.UNDEFINED;
    }
}
