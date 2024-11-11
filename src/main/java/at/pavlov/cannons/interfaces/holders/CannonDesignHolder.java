package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonDesign;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.interfaces.functionalities.Updatable;

public interface CannonDesignHolder extends Updatable {
    void setCannonDesign(CannonDesign design);

    CannonDesign getCannonDesign();

    default boolean sameDesign(Cannon cannon) {
        return sameDesign(cannon.getCannonDesign());
    }

    default boolean sameDesign(CannonDesign cannonDesign) {
        return getCannonDesign().equals(cannonDesign);
    }

    default String getDesignID() {
        return getCannonDesign().getDesignID();
    }

    default void setDesignID(String designID) {
        setCannonDesign(DesignStorage.getInstance().getDesign(designID));
        this.hasUpdated();
    }

}
