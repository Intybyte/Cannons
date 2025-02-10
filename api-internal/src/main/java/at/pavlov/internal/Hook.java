package at.pavlov.internal;

import org.jetbrains.annotations.NotNull;

public interface Hook<H> {
    H hook();
    default boolean active() {
        return hook() != null;
    }

    void onEnable();
    void onDisable();
    Class<? extends Hook<?>> getTypeClass();

    default @NotNull String enabledMessage() {
        return getTypeClass().getSimpleName() + " Enabled.";
    }
}
