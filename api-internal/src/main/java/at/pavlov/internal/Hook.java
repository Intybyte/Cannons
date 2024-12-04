package at.pavlov.internal;

public interface Hook<H> {
    H hook();
    default boolean active() {
        return hook() != null;
    }

    void onEnable();
    void onDisable();
    Class<? extends Hook<?>> getTypeClass();

    default String enabledMessage() {
        return getTypeClass().getSimpleName() + " Enabled.";
    }
}
