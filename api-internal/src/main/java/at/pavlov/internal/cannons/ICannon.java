package at.pavlov.internal.cannons;

import at.pavlov.internal.cannons.functionalities.CannonDataHolder;
import at.pavlov.internal.cannons.functionalities.InventoryObject;

public interface ICannon<I, D, V, Pr, Pl> extends CannonDataHolder<D, V, Pr, Pl>, InventoryObject<I> {
    boolean sameType(ICannon<I, D, V, Pr, Pl> cannon);
}
