package at.pavlov.internal;

public interface Hook<H> {
    H hook();
    default boolean working() {
        return hook() != null;
    }

    void onEnable();
    void onDisable();
    Class<? extends Hook<?>> getTypeClass();
}
