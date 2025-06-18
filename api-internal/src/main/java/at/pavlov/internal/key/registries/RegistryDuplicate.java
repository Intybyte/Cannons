package at.pavlov.internal.key.registries;

public class RegistryDuplicate extends RuntimeException {
    public RegistryDuplicate(String message) {
        super(message);
    }
}
