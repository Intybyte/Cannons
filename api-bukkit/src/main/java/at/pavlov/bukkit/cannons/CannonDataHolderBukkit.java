package at.pavlov.bukkit.cannons;

import at.pavlov.bukkit.cannons.holders.CannonPositionHBukkit;
import at.pavlov.bukkit.projectile.Projectile;
import at.pavlov.internal.cannons.holders.AimingDataHolder;
import at.pavlov.internal.cannons.holders.AmmoLoadingDataHolder;
import at.pavlov.internal.cannons.holders.AngleDataHolder;
import at.pavlov.internal.cannons.holders.CannonMainDataHolder;
import at.pavlov.internal.cannons.holders.FiringDataHolder;
import at.pavlov.internal.cannons.holders.LinkingDataHolder;
import at.pavlov.internal.cannons.holders.ObserverMapHolder;
import at.pavlov.internal.cannons.holders.SentryDataHolder;
import at.pavlov.internal.cannons.holders.WhitelistDataHolder;
import org.bukkit.entity.Player;

public interface CannonDataHolderBukkit extends
        AimingDataHolder,
        AmmoLoadingDataHolder<Player, Projectile>,
        AngleDataHolder,
        CannonMainDataHolder,
        CannonPositionHBukkit,
        FiringDataHolder<Projectile>,
        LinkingDataHolder,
        ObserverMapHolder,
        SentryDataHolder,
        WhitelistDataHolder,
        CannonDesignHolder
{}
