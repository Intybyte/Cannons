package at.pavlov.cannons.cannon;

import at.pavlov.cannons.container.SimpleBlock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.util.Vector;

import java.util.ArrayList;

/**
 * This class is used internally to keep track of some blocks,
 * it is not suggested to create new instances of it but instead
 * use it to get the positions of the various stuff
 */
@Setter
@Getter
public class CannonBlocks {
	private Vector rotationCenter;	//center off all rotation blocks
    private Vector muzzle;			//center off all muzzle blocks - spawing Vector for snowball

	private ArrayList<SimpleBlock> allCannonBlocks = new ArrayList<>();
    private ArrayList<Vector> barrelBlocks = new ArrayList<>();
    private ArrayList<SimpleBlock> chestsAndSigns = new ArrayList<>();
    private ArrayList<Vector> redstoneTorches = new ArrayList<>();
    private ArrayList<SimpleBlock> redstoneWiresAndRepeater = new ArrayList<>();
    private ArrayList<Vector> redstoneTrigger = new ArrayList<>();
    private ArrayList<Vector> rightClickTrigger = new ArrayList<>();
    private ArrayList<Vector> firingIndicator = new ArrayList<>();
    private ArrayList<Vector> destructibleBlocks = new ArrayList<>();

    /**
     * returns true if this block is part of the loading interface
     * @param loc
     * @return
     */
    public boolean isLoadingInterface(Vector loc) {
    	for (Vector loadingBlock : barrelBlocks) {
    		if (loc.equals(loadingBlock)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * returns the location off one firing Trigger
     * @return the firing trigger. (can be null if there is no trigger on the cannon)
     */
    public Vector getFiringTrigger() {
    	//return one tigger
    	if (rightClickTrigger!= null && !rightClickTrigger.isEmpty())
    		return rightClickTrigger.get(0);
    	if (redstoneTrigger != null && !redstoneTrigger.isEmpty())
        	return redstoneTrigger.get(0);
        return null;
    }

    public void addBarrelBlocks(Vector add) {
		this.barrelBlocks.add(add);
	}

    public void addRedstoneTorch(Vector add) {
		this.redstoneTorches.add(add);
	}

    public void addRedstoneTrigger(Vector add) {
		this.redstoneTrigger.add(add);
	}

    public void addRightClickTrigger(Vector add) {
		this.rightClickTrigger.add(add);
	}

    public void addChestsAndSigns(SimpleBlock add) {
		this.chestsAndSigns.add(add);
	}

    public void addRedstoneWiresAndRepeater(SimpleBlock add) {
		this.redstoneWiresAndRepeater.add(add);
	}

    public void addFiringIndicator(Vector add) {
		this.firingIndicator.add(add);
	}

    public void addDestructibleBlocks(Vector add) {
		this.destructibleBlocks.add(add);
	}


    private Vector min = null;
    public Vector getMin() {
        if (min != null) return min;
        calculateMaxMin();
        return min;
    }

    private Vector max = null;
    public Vector getMax() {
        if (max != null) return max;
        calculateMaxMin();
        return max;
    }

    private void calculateMaxMin() {
        Vector minT = allCannonBlocks.get(0).toVector();
        Vector maxT = minT.clone();

        for (var block : allCannonBlocks) {
            Vector vec = block.toVector();

            minT.setX(Math.min(minT.getX(), vec.getX()));
            minT.setY(Math.min(minT.getY(), vec.getY()));
            minT.setZ(Math.min(minT.getZ(), vec.getZ()));

            maxT.setX(Math.max(maxT.getX(), vec.getX()));
            maxT.setY(Math.max(maxT.getY(), vec.getY()));
            maxT.setZ(Math.max(maxT.getZ(), vec.getZ()));
        }

        min = minT;
        max = maxT;
    }

    private double diagonal = -1;
    public double getDiagonal() {
        if (diagonal < 0) {
            calculateMaxMin();
            diagonal = max.distance(min);
        }

        return diagonal;
    }
}