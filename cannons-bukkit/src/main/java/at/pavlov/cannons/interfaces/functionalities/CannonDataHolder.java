package at.pavlov.cannons.interfaces.functionalities;

import at.pavlov.cannons.interfaces.holders.AimingDataHolder;
import at.pavlov.cannons.interfaces.holders.AmmoLoadingDataHolder;
import at.pavlov.cannons.interfaces.holders.AngleDataHolder;
import at.pavlov.cannons.interfaces.holders.CannonDesignHolder;
import at.pavlov.cannons.interfaces.holders.CannonMainDataHolder;
import at.pavlov.cannons.interfaces.holders.CannonPositionHolder;
import at.pavlov.cannons.interfaces.holders.FiringDataHolder;
import at.pavlov.cannons.interfaces.holders.LinkingDataHolder;
import at.pavlov.cannons.interfaces.holders.ObserverMapHolder;
import at.pavlov.cannons.interfaces.holders.SentryDataHolder;
import at.pavlov.cannons.interfaces.holders.WhitelistDataHolder;

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
