package at.pavlov.internal;

public interface Hook<H> {
    H hook();
    void onEnable();
    void onDisable();
    Class<? extends Hook> getTypeClass();
}
