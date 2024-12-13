package at.pavlov.internal.container.location;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data public class Coordinate {
    private CannonVector vector;
    private UUID world;

    public Coordinate(double x, double y, double z, UUID world) {
        this.vector = new CannonVector(x, y, z);
        this.world = world;
    }
}
