package at.pavlov.cannons.hooks;

import at.pavlov.cannons.Cannons;
import at.pavlov.internal.Hook;

abstract public class BukkitHook<P> implements Hook<P> {

    protected Cannons plugin;
    protected P hook;

    public BukkitHook(Cannons plugin) {
        this.plugin = plugin;
    }

    @Override
    public P hook() {
        return hook;
    }
}
