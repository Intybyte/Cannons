package at.pavlov.bukkit.cannons;

import at.pavlov.bukkit.cannons.holders.CannonDesignHBukkit;
import at.pavlov.bukkit.cannons.holders.CannonPositionHBukkit;
import at.pavlov.bukkit.projectile.BukkitProjectile;
import at.pavlov.internal.cannons.functionalities.CannonDataHolder;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public interface CannonDataHolderBukkit extends
        CannonDataHolder<BlockFace, BukkitProjectile, Player>,
        CannonPositionHBukkit,
        CannonDesignHBukkit
{}
