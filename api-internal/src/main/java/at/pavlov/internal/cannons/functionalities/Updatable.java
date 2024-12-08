package at.pavlov.internal.cannons.functionalities;

public interface Updatable {
    default void hasUpdated() {
        setUpdated(true);
    }
    boolean isUpdated();
    void setUpdated(boolean updated);
}
