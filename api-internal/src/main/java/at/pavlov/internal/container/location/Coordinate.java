package at.pavlov.internal.container.location;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Coordinate {
    private CannonVector vector;
    private UUID world;

    public Coordinate(UUID world, double x, double y, double z) {
        this.world = world;
        this.vector = new CannonVector(x, y, z);
    }
}
