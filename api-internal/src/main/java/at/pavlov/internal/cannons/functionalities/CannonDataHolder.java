package at.pavlov.internal.cannons.functionalities;

import at.pavlov.internal.cannons.holders.AimingDataHolder;
import at.pavlov.internal.cannons.holders.AmmoLoadingDataHolder;
import at.pavlov.internal.cannons.holders.AngleDataHolder;
import at.pavlov.internal.cannons.holders.CannonMainDataHolder;
import at.pavlov.internal.cannons.holders.CannonPositionHolder;
import at.pavlov.internal.cannons.holders.FiringDataHolder;
import at.pavlov.internal.cannons.holders.LinkingDataHolder;
import at.pavlov.internal.cannons.holders.ObserverMapHolder;
import at.pavlov.internal.cannons.holders.SentryDataHolder;
import at.pavlov.internal.cannons.holders.WhitelistDataHolder;

public interface CannonDataHolder<Dir, Prj, Ply> extends
        FiringDataHolder<Prj>,
        AimingDataHolder,
        SentryDataHolder,
        CannonPositionHolder<Dir>,
        AmmoLoadingDataHolder<Ply, Prj>,
        WhitelistDataHolder,
        CannonMainDataHolder,
        LinkingDataHolder,
        AngleDataHolder,
        ObserverMapHolder
{}
