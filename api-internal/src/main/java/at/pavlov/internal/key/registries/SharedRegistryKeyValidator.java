package at.pavlov.internal.key.registries;

import at.pavlov.internal.key.Key;
import at.pavlov.internal.key.KeyHolder;
import at.pavlov.internal.key.registries.exceptions.RegistryException;
import at.pavlov.internal.key.registries.exceptions.RegistryValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SharedRegistryKeyValidator<T extends KeyHolder> implements RegistryValidator<T> {
    private final List<RegistryAccess<? extends T>> list = new ArrayList<>();

    @SafeVarargs
    public SharedRegistryKeyValidator(RegistryAccess<? extends T>... registries) {
        list.addAll(Arrays.asList(registries));
    }

    @Override
    public void test(Key key, T value) throws RegistryException {
        for (RegistryAccess<? extends T> registry : list) {
            if (registry.has(key)) {
                throw new RegistryException("Duplicate key detected across registries: " + key);
            }
        }
    }
}
