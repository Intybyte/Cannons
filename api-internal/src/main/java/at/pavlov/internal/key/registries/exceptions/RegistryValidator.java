package at.pavlov.internal.key.registries.exceptions;

import at.pavlov.internal.key.Key;
import at.pavlov.internal.key.KeyHolder;

public interface RegistryValidator<T extends KeyHolder> {
    void test(Key key, T value) throws RegistryException;
}
