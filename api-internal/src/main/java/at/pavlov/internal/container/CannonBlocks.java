package at.pavlov.internal.container;

import at.pavlov.internal.container.holders.BlockHolder;
import at.pavlov.internal.container.location.CannonVector;
import lombok.Data;

import java.util.ArrayList;

/**
 * This class is used internally to keep track of some blocks,
 * it is not suggested to create new instances of it but instead
 * use it to get the positions of the various stuff
 */
@Data public abstract class CannonBlocks<Block extends BlockHolder<?>> {
	private CannonVector rotationCenter;	//center off all rotation blocks
    private CannonVector muzzle;			//center off all muzzle blocks - spawing CannonVector for snowball

	private ArrayList<Block> allCannonBlocks = new ArrayList<>();
    private ArrayList<CannonVector> barrelBlocks = new ArrayList<>();
    private ArrayList<Block> chestsAndSigns = new ArrayList<>();
    private ArrayList<CannonVector> redstoneTorches = new ArrayList<>();
    private ArrayList<Block> redstoneWiresAndRepeater = new ArrayList<>();
    private ArrayList<CannonVector> redstoneTrigger = new ArrayList<>();
    private ArrayList<CannonVector> rightClickTrigger = new ArrayList<>();
    private ArrayList<CannonVector> firingIndicator = new ArrayList<>();
    private ArrayList<CannonVector> destructibleBlocks = new ArrayList<>();

    /**
     * returns true if this block is part of the loading interface
     */
    public boolean isLoadingInterface(CannonVector loc) {
    	for (CannonVector loadingBlock : barrelBlocks) {
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
    public CannonVector getFiringTrigger() {
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

    public void addBarrelBlocks(CannonVector add) {
		this.barrelBlocks.add(add);
	}

    public void addRedstoneTorch(CannonVector add) {
		this.redstoneTorches.add(add);
	}

    public void addRedstoneTrigger(CannonVector add) {
		this.redstoneTrigger.add(add);
	}

    public void addRightClickTrigger(CannonVector add) {
		this.rightClickTrigger.add(add);
	}

    public void addChestsAndSigns(Block add) {
		this.chestsAndSigns.add(add);
	}

    public void addRedstoneWiresAndRepeater(Block add) {
		this.redstoneWiresAndRepeater.add(add);
	}

    public void addFiringIndicator(CannonVector add) {
		this.firingIndicator.add(add);
	}

    public void addDestructibleBlocks(CannonVector add) {
		this.destructibleBlocks.add(add);
	}
    
}