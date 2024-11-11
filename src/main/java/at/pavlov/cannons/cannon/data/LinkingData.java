package at.pavlov.cannons.cannon.data;

import lombok.Data;

import java.util.UUID;

@Data public class LinkingData {
    // cannon operator (can be null), distance to the cannon matters
    private UUID cannonOperator;
    // linked cannon operator is controling cannon via a master cannon.
    private boolean masterCannon;
}
