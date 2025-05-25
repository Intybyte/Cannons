package mockbukkit;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

public class RegistryMock<T extends Keyed> implements Registry<T> {

    /**
     * These classes have registries that are an exception to the others, as they are wrappers to minecraft internals
     */
    private final Map<NamespacedKey, T> keyedMap = new HashMap<>();
    private JsonArray keyedData;
    private Function<JsonObject, T> constructor;

    public RegistryMock(NamespacedKey key) {
        try {
            loadKeyedToRegistry(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadKeyedToRegistry(@NotNull NamespacedKey key) throws IOException {
        String fileName = "/keyed/" + key.getKey() + ".json";
        this.constructor = (Function<JsonObject, T>) getConstructorFunction(key);
        keyedData = ResourceLoader.loadResource(fileName).getAsJsonObject().get("values").getAsJsonArray();
    }

    private Function<JsonObject, ? extends Keyed> getConstructorFunction(NamespacedKey key) {
        Map<NamespacedKey, Function<JsonObject, ? extends Keyed>> factoryMap = new HashMap<>();
        factoryMap.put(RegistryAccessMock.MOB_EFFECT, PotionEffectTypeMock::from);

        Function<JsonObject, ? extends Keyed> constructorFunction = factoryMap.get(key);
        if (constructorFunction == null) {
            throw new RuntimeException();
        }

        return constructorFunction;
    }

    @Override
    public @Nullable T get(@NotNull NamespacedKey key) {
        Preconditions.checkNotNull(key);
        loadIfEmpty();
        return keyedMap.get(key);
    }

    @Override
    public @NotNull T getOrThrow(@NotNull NamespacedKey namespacedKey) {
        Preconditions.checkNotNull(namespacedKey);
        loadIfEmpty();
        T value = this.keyedMap.get(namespacedKey);
        if (value == null) {
            throw new java.util.NoSuchElementException("No value for " + namespacedKey + " in " + this);
        }
        return value;
    }

    @Override
    public @NotNull Stream<T> stream() {
        loadIfEmpty();
        return keyedMap.values().stream();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        loadIfEmpty();
        return keyedMap.values().iterator();
    }

    private void loadIfEmpty() {
        if (keyedMap.isEmpty()) {
            for (JsonElement structureJSONElement : keyedData) {
                JsonObject structureJSONObject = structureJSONElement.getAsJsonObject();
                T tObject = constructor.apply(structureJSONObject);
                keyedMap.putIfAbsent(tObject.getKey(), tObject);
            }
        }
    }

}