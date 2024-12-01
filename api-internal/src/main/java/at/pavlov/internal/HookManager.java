package at.pavlov.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HookManager {
    private final Map<Class<? extends Hook<?>>, Hook<?>> hooks = new HashMap<>();

    /**
     * @return A defensive copy of the hook map
     */
    public Map<Class<? extends Hook<?>>, Hook<?>> hookMap() {
        return Collections.unmodifiableMap(hooks);
    }

    public boolean isRegistered(Class<? extends Hook<?>> type) {
        if (this.hooks.containsKey(type)) {
            return true;
        }

        for (Class<? extends Hook<?>> clazz : hooks.keySet()) {
            if (type.isAssignableFrom(clazz)) {
                return true;
            }
        }

        return false;
    }

    public <T extends Hook<?>> T getHook(Class<T> type) {
        Hook<?> hook = this.hooks.get(type);
        if (hook != null) {
            return type.cast(hook);
        }

        for (Class<? extends Hook<?>> clazz : hooks.keySet()) {
            if (type.isAssignableFrom(clazz)) {
                hook = hooks.get(clazz);
            }
        }

        if (hook == null) {
            throw new IllegalArgumentException("No registered hook of type " + type.getName() + "!");
        }
        return type.cast(hook);
    }

    /**
     * @return true if at least one hook in the manager is working
     */
    public boolean isActive() {
        return hooks.values().stream().anyMatch(Hook::active);
    }

    public void registerHook(Hook<?> hook) {
        hook.onEnable();
        this.hooks.put(hook.getTypeClass(), hook);
    }

    public void disableHooks() {
        for (Hook<?> hook : hooks.values()) {
            hook.onDisable();
        }
    }
}
