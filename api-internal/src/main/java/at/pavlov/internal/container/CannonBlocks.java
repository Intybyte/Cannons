package at.pavlov.internal.container;

import lombok.Data;

import java.util.ArrayList;

/**
 * This class is used internally to keep track of some blocks,
 * it is not suggested to create new instances of it but instead
 * use it to get the positions of the various stuff
 */
@Data public abstract class CannonBlocks<Vector, Block extends SimpleBlock<?>> {
	private Vector rotationCenter;	//center off all rotation blocks
    private Vector muzzle;			//center off all muzzle blocks - spawing Vector for snowball

	private ArrayList<Block> allCannonBlocks = new ArrayList<>();
    private ArrayList<Vector> barrelBlocks = new ArrayList<>();
    private ArrayList<Block> chestsAndSigns = new ArrayList<>();
    private ArrayList<Vector> redstoneTorches = new ArrayList<>();
    private ArrayList<Block> redstoneWiresAndRepeater = new ArrayList<>();
    private ArrayList<Vector> redstoneTrigger = new ArrayList<>();
    private ArrayList<Vector> rightClickTrigger = new ArrayList<>();
    private ArrayList<Vector> firingIndicator = new ArrayList<>();
    private ArrayList<Vector> destructibleBlocks = new ArrayList<>();

    /**
     * returns true if this block is part of the loading interface
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

    public void addAllCannonBlocks(Block add) {
		this.allCannonBlocks.add(add);
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

    public void addChestsAndSigns(Block add) {
		this.chestsAndSigns.add(add);
	}

    public void addRedstoneWiresAndRepeater(Block add) {
		this.redstoneWiresAndRepeater.add(add);
	}

    public void addFiringIndicator(Vector add) {
		this.firingIndicator.add(add);
	}

    public void addDestructibleBlocks(Vector add) {
		this.destructibleBlocks.add(add);
	}
    
}