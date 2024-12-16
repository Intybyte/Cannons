package at.pavlov.internal.container;


import at.pavlov.internal.container.location.CannonVector;
import lombok.Data;

import java.util.function.Function;

@Data abstract public class SimpleBlock<Block> {
    protected int locX;
	protected int locY;
	protected int locZ;
	protected boolean directional;

	protected Block blockData;

	public SimpleBlock(int x, int y, int z, Block blockData) {
		locX = x;
		locY = y;
		locZ = z;

		this.blockData = blockData;
		this.directional = directionalCheck().apply(blockData);
	}

	public abstract Function<Block, Boolean> directionalCheck();

	/**
	 * return true if Materials match
	 * @param block block to compare to
	 * @return true if both block match
	 */
	public abstract boolean compareMaterial(Block block);

	/**
	 * SimpleBlock to Vector
	 */
	public CannonVector toVector() {
		return new CannonVector(locX, locY, locZ);
	}

	/**
	 * compares material and facing
	 * @param blockData block to compare to
	 * @return true if both block match
	 */
	public abstract boolean compareMaterialAndFacing(Block blockData);

    public String toString() {
		return "x:" + locX + " y:" + locY + " z:" + locZ +" blockdata:" + this.getBlockData().toString();
	}
}
