package at.pavlov.cannons.exchange;

import lombok.Getter;
import org.bukkit.entity.Player;

public record VaultExchanger(double money) implements BExchanger{

    @Override
    public void take(Player player) {

    }

    @Override
    public void give(Player player) {

    }
}
