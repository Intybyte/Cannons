package at.pavlov.cannons.cannon.data;

import lombok.Data;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.UUID;

@Data public class CannonPosition {
    // direction the cannon is facing
    private BlockFace cannonDirection;
    // the location is describe by the offset of the cannon and the design
    private Vector offset;
    // world of the cannon
    private UUID world;
    // if the cannon is on a ship, the operation might be limited (e.g smaller angles to adjust the cannon)
    private boolean onShip;
    // with which velocity the canno is moving (set by other plugins)
    private Vector velocity;
}
