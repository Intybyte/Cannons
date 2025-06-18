package at.pavlov.internal.registries;

public class RegistryDuplicate extends RuntimeException {
    public RegistryDuplicate(String message) {
        super(message);
    }
}
