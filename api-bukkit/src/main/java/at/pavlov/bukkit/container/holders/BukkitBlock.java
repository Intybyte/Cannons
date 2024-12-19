package at.pavlov.bukkit.container.holders;


import at.pavlov.internal.container.holders.BlockHolder;
import at.pavlov.internal.container.location.CannonVector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.Directional;
import org.bukkit.util.Vector;

import java.util.function.Function;

public class BukkitBlock extends BlockHolder<BlockData> {

    public BukkitBlock(int x, int y, int z, BlockData blockData) {
        super(x, y, z, blockData);
    }

    @Override
    public Function<BlockData, Boolean> directionalCheck() {
        return Directional.class::isInstance;
    }

    public BukkitBlock(CannonVector vect, BlockData blockData) {
        this(vect.getBlockX(), vect.getBlockY(), vect.getBlockZ(), blockData);
    }

    public BukkitBlock(Vector vect, BlockData blockData) {
        this(vect.getBlockX(), vect.getBlockY(), vect.getBlockZ(), blockData);
    }

    public BukkitBlock(int x, int y, int z, Material material) {
        this(x, y, z, material.createBlockData());
    }

    private BukkitBlock(CannonVector vect, Material material) {
        this(vect, material.createBlockData());
    }

    public BukkitBlock(Location loc, Material material) {
		this(loc.toVector(), material.createBlockData());
    }


    /**
     * to location with offset
     *
     * @param world bukkit world
     * @return location of the block
     */
    public Location toLocation(World world, CannonVector offset) {
        return new Location(world, locX + offset.getBlockX(), locY + offset.getBlockY(), locZ + offset.getBlockZ());
    }


    /**
     * compare the location of the block and the id and data or data = -1
     *
     * @param block  block to compare to
     * @param offset the offset of the cannon
     * @return true if both block match
     */
    public boolean compareMaterialAndLoc(Block block, CannonVector offset) {
        if (!toVector().add(offset).equals(block.getLocation().toVector())) {
            return false;
        }

        return compareMaterial(block.getBlockData());
    }

    /**
     * return true if Materials match
     *
     * @param block block to compare to
     * @return true if both block match
     */
    public boolean compareMaterial(BlockData block) {
        return block.getMaterial().equals(this.blockData.getMaterial());
    }

    /**
     * compares material and facing
     *
     * @param blockData block to compare to
     * @return true if both block match
     */
    public boolean compareMaterialAndFacing(BlockData blockData) {
        // different materials
        if (!compareMaterial(blockData)) {
            return false;
        }
        // compare facing and face
        if (directional && blockData instanceof Directional) {
            return ((Directional) this).getFacing().equals(((Directional) blockData).getFacing());
        }
        return true;
    }

    /**
     * compares the real world block by material and facing
     *
     * @param world  the world of the block
     * @param offset the locations in x,y,z
     * @return true if both block are equal in data and facing
     */
    public boolean compareMaterialAndFacing(World world, CannonVector offset) {
        Block block = toLocation(world, offset).getBlock();
        return compareMaterialAndFacing(block.getBlockData());
    }

    /**
     * matches all entries in this SimpleBlock to the given block
     *
     * @param blockData block to compare to
     * @return true if both block match
     */
    public boolean compareBlockData(BlockData blockData) {
        return this.blockData.matches(blockData);
    }

    /**
     * shifts the location of the block without comparing the id
     *
     * @param loc location to add
     * @return new Simpleblock
     */
    public BukkitBlock add(Location loc) {
        return new BukkitBlock(locX + loc.getBlockX(), locY + loc.getBlockY(), locZ + loc.getBlockZ(), this.blockData);
    }

    /**
     * shifts the location of the block without comparing the id
     *
     * @param vect offset vector
     * @return a new block with a shifted location
     */
    public BukkitBlock add(CannonVector vect) {
        return new BukkitBlock(toVector().add(vect), this.blockData);
    }

    /**
     * shifts the location of the block without comparing the id
     *
     * @param vect vector to subtract
     * @return new block with new subtracted location
     */
    public BukkitBlock subtract(Vector vect) {
        return new BukkitBlock(vect.getBlockX() - locX, vect.getBlockY() - locY, vect.getBlockZ() - locZ, this.blockData);
    }

    /**
     * shifts the location of the block without comparing the id
     *
     * @param vect vector to subtract
     */
    public void subtract_noCopy(CannonVector vect) {
        locX -= vect.getBlockX();
        locY -= vect.getBlockY();
        locZ -= vect.getBlockZ();
    }

    /**
     * shifts the location of the block without comparing the id
     *
     * @param loc
     */
    public BukkitBlock subtractInverted(Location loc) {
        return new BukkitBlock(loc.getBlockX() - locX, loc.getBlockY() - locY, loc.getBlockZ() - locZ, this.blockData);
    }


    /**
     * shifts the location of the block without comparing the id
     *
     * @param loc
     */
    public BukkitBlock subtract(Location loc) {
        return new BukkitBlock(locX - loc.getBlockX(), locY - loc.getBlockY(), locZ - loc.getBlockZ(), this.blockData);
    }

    /**
     * rotate the block 90° degree clockwise)
     *
     * @return
     */
    public void rotate90() {
        this.blockData = rotateBlockFacingClockwise(this.blockData);
        int newx = -this.locZ;
        this.locZ = this.locX;
        this.locX = newx;
    }

    public static BlockData rotateBlockFacingClockwise(BlockData blockData) {
        if (blockData instanceof org.bukkit.block.data.Directional directional) {
            directional.setFacing(rotateFace(directional.getFacing()));
        }
        return blockData;
    }

    public static BlockFace rotateFace(BlockFace face) {
        if (face.equals(BlockFace.NORTH)) return BlockFace.EAST;
        if (face.equals(BlockFace.EAST)) return BlockFace.SOUTH;
        if (face.equals(BlockFace.SOUTH)) return BlockFace.WEST;
        if (face.equals(BlockFace.WEST)) return BlockFace.NORTH;
        return BlockFace.UP;
    }

}
