package mockbukkit;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistryAccessMock {
    public static final NamespacedKey MOB_EFFECT = NamespacedKey.minecraft("mob_effect");

    private static final BiMap<NamespacedKey, String> CLASS_NAME_KEY_MAP = createClassToKeyConversions();
    private final Map<NamespacedKey, Registry<?>> registries = new HashMap<>();

    private static <T extends Keyed> Registry<@NotNull T> createRegistry(NamespacedKey key) {
        if (getOutlierKeyedRegistryKeys().contains(key)) {
            return new RegistryMock<>(key);
        }
        return new EmptyRegistry<>();
    }

    private static List<NamespacedKey> getOutlierKeyedRegistryKeys() {
        return List.of(MOB_EFFECT);
    }

    private static @NotNull BiMap<NamespacedKey, String> createClassToKeyConversions() {
        String fileName = "/registries/registry_key_class_relation.json";
        JsonObject object = ResourceLoader.loadResource(fileName).getAsJsonObject();

        BiMap<NamespacedKey, String> output = HashBiMap.create();
        for (NamespacedKey registryKey : getOutlierKeyedRegistryKeys()) {
            JsonElement element = object.get(registryKey.toString());
            if (element != null) {
                String className = element.getAsString();
                output.put(registryKey, className);
            } else {
                throw new RuntimeException("Null JSON element while retrieving `" + registryKey + "` - MockBukkit / MC version mismatch?");
            }
        }
        return output;
    }


    @Deprecated(forRemoval = true, since = "1.20.6")
    public @Nullable <T extends Keyed> Registry<@NotNull T> getRegistry(@NotNull Class<T> type) {
        NamespacedKey registryKey = determineRegistryKeyFromClass(type);
        if (registryKey == null) {
            return new EmptyRegistry<>();
        }

        return getRegistry(registryKey);
    }

    private <T extends Keyed> NamespacedKey determineRegistryKeyFromClass(@NotNull Class<T> type) {
        return CLASS_NAME_KEY_MAP.inverse().get(type.getName());
    }

    public @NotNull <T extends Keyed> Registry<T> getRegistry(@NotNull NamespacedKey registryKey) {
        if (registries.containsKey(registryKey)) {
            return (Registry<T>) registries.get(registryKey);
        }
        Registry<T> registry = createRegistry(registryKey);
        registries.put(registryKey, registry);
        return registry;
    }

}