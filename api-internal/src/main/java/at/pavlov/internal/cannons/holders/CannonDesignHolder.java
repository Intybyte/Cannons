package at.pavlov.internal.cannons.holders;

import at.pavlov.internal.cannons.data.CannonDesign;
import at.pavlov.internal.cannons.functionalities.Updatable;

public interface CannonDesignHolder<T extends CannonDesign<?, ?, ?, ?, ?, ?, ?>> extends Updatable {
    void setCannonDesign(T design);
    T getCannonDesign();

    default <D extends CannonDesignHolder<?>> boolean sameDesign(D cannon) {
        return sameDesign(cannon.getCannonDesign());
    }

    default <D extends CannonDesign<?, ?, ?, ?, ?, ?, ?>> boolean sameDesign(D cannonDesign) {
        return getCannonDesign().equals(cannonDesign);
    }

    default String getDesignID() {
        return getCannonDesign().getDesignID();
    }

    void setDesignID(String designID);

}
