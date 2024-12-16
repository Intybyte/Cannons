package at.pavlov.bukkit.cannons;

import at.pavlov.bukkit.cannons.data.BukkitCannonDesign;
import at.pavlov.internal.cannons.functionalities.Updatable;

public interface CannonDesignHolder extends Updatable {
    void setCannonDesign(BukkitCannonDesign design);

    BukkitCannonDesign getCannonDesign();

    default boolean sameDesign(CannonBukkit cannon) {
        return sameDesign(cannon.getCannonDesign());
    }

    default boolean sameDesign(BukkitCannonDesign cannonDesign) {
        return getCannonDesign().equals(cannonDesign);
    }

    default String getDesignID() {
        return getCannonDesign().getDesignID();
    }

    void setDesignID(String designID);

}
