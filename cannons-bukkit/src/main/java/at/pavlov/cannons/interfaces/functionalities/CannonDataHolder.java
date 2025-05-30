package at.pavlov.cannons.interfaces.functionalities;

import at.pavlov.internal.cannon.holders.AimingDataHolder;
import at.pavlov.cannons.interfaces.holders.AmmoLoadingDataHolder;
import at.pavlov.internal.cannon.holders.AngleDataHolder;
import at.pavlov.cannons.interfaces.holders.CannonDesignHolder;
import at.pavlov.internal.cannon.holders.CannonMainDataHolder;
import at.pavlov.cannons.interfaces.holders.CannonPositionHolder;
import at.pavlov.cannons.interfaces.holders.FiringDataHolder;
import at.pavlov.cannons.interfaces.holders.LinkingDataHolder;
import at.pavlov.cannons.interfaces.holders.ObserverMapHolder;
import at.pavlov.internal.cannon.holders.SentryDataHolder;
import at.pavlov.internal.cannon.holders.WhitelistDataHolder;

public interface CannonDataHolder extends
        FiringDataHolder,
        AimingDataHolder,
        SentryDataHolder,
        CannonPositionHolder,
        AmmoLoadingDataHolder,
        WhitelistDataHolder,
        CannonDesignHolder,
        CannonMainDataHolder,
        LinkingDataHolder,
        AngleDataHolder,
        ObserverMapHolder
{}
