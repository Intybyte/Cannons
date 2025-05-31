package at.pavlov.internal.cannon.functionalities;

public interface Updatable {
    default void hasUpdated() {
        setUpdated(true);
    }
    boolean isUpdated();
    void setUpdated(boolean updated);
}
