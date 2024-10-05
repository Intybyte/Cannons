package at.pavlov.cannons.interfaces;

public interface Updatable {
    default void hasUpdated() {
        setUpdated(true);
    }
    boolean isUpdated();
    void setUpdated(boolean updated);
}
