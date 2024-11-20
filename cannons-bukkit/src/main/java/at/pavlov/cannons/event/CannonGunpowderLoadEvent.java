package at.pavlov.cannons.event;

import at.pavlov.cannons.cannon.Cannon;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class CannonGunpowderLoadEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Cannon cannon;
    private final int startAmount;
    private final int amountToLoad;
    private final int newAmount;
    private final Player player;

    public CannonGunpowderLoadEvent(Cannon cannon, int startAmount, int amountToLoad, int newAmount, Player player) {
        this.cannon = cannon;
        this.startAmount = startAmount;
        this.amountToLoad = amountToLoad;
        this.newAmount = newAmount;
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public int getAmountToLoad() {
        return amountToLoad;
    }

    public int getNewAmount() {
        return newAmount;
    }

    public int getStartAmount() {
        return startAmount;
    }

    public Player getPlayer() {
        return player;
    }
}
