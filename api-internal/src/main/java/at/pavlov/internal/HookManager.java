package at.pavlov.internal;

import java.util.HashMap;
import java.util.Map;

public class HookManager {
    private final Map<Class<? extends Hook>, Hook> hooks = new HashMap<>();

    public boolean isRegistered(Class<? extends Hook> type) {
        if (this.hooks.containsKey(type)) {
            return true;
        }

        for (Class<? extends Hook> clazz : hooks.keySet()) {
            if (type.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    public <T extends Hook> T getHook(Class<T> type) {
        Hook hook = this.hooks.get(type);
        if (hook != null) {
            return type.cast(hook);
        }

        for (Class<? extends Hook> clazz : hooks.keySet()) {
            if (type.isAssignableFrom(clazz)) {
                hook = hooks.get(clazz);
            }
        }

        if (hook == null) {
            throw new IllegalArgumentException("No registered hook of type " + type.getName() + "!");
        }
        return type.cast(hook);
    }

    public void registerHook(Class<? extends Hook> type, Hook hook) {
        hook.onEnable();
        this.hooks.put(type, hook);
    }

    public void disableHooks() {
        for (Hook hook : hooks.values()) {
            hook.onDisable();
        }
    }
}
