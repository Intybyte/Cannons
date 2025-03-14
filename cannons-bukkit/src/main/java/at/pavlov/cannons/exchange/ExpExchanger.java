package at.pavlov.cannons.exchange;

import org.bukkit.entity.Player;

public record ExpExchanger(int expAmount) implements BExchanger {

    @Override
    public void take(Player player) {

    }

    @Override
    public void give(Player player) {

    }
}
