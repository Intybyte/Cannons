package at.pavlov.cannons.interfaces.holders;

import at.pavlov.internal.cannon.data.CannonMainData;
import at.pavlov.internal.cannon.functionalities.Updatable;

import java.util.UUID;

public interface CannonMainDataHolder extends Updatable {
    CannonMainData getCannonMainData();
    void setCannonMainData(CannonMainData data);

    default UUID getUID() {
        return getCannonMainData().getDatabaseId();
    }

    default void setUID(UUID ID) {
        getCannonMainData().setDatabaseId(ID);
        this.hasUpdated();
    }

    default String getCannonName() {
        return getCannonMainData().getCannonName();
    }

    default void setCannonName(String name) {
        getCannonMainData().setCannonName(name);
        this.hasUpdated();
    }

    default boolean isValid() {
        return getCannonMainData().isValid();
    }

    default void setValid(boolean isValid) {
        getCannonMainData().setValid(isValid);
        this.hasUpdated();
    }

    default UUID getOwner() {
        return getCannonMainData().getOwner();
    }

    default void setOwner(UUID owner) {
        getCannonMainData().setOwner(owner);
        this.hasUpdated();
    }

    default boolean isPaid() {
        return getCannonMainData().isPaid();
    }

    default void setPaid(boolean paid) {
        getCannonMainData().setPaid(paid);
        this.hasUpdated();
    }
}
