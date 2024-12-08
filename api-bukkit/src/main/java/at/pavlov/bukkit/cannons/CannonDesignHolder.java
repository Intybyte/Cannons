package at.pavlov.bukkit.cannons;

import at.pavlov.internal.cannons.functionalities.Updatable;

public interface CannonDesignHolder extends Updatable {
    void setCannonDesign(CannonDesign design);

    CannonDesign getCannonDesign();

    default boolean sameDesign(CannonBukkit cannon) {
        return sameDesign(cannon.getCannonDesign());
    }

    default boolean sameDesign(CannonDesign cannonDesign) {
        return getCannonDesign().equals(cannonDesign);
    }

    default String getDesignID() {
        return getCannonDesign().getDesignID();
    }

    void setDesignID(String designID);

}
