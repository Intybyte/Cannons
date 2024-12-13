package at.pavlov.bukkit.container;

import at.pavlov.internal.container.CannonBlocks;
import org.bukkit.util.Vector;

/**
 * This class is used internally to keep track of some blocks,
 * it is not suggested to create new instances of it but instead
 * use it to get the positions of the various stuff
 */
public class BukkitCannonBlocks extends CannonBlocks<Vector, BukkitBlock> {
}