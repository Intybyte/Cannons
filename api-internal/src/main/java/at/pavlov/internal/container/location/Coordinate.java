package at.pavlov.internal.container.location;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Coordinate implements Cloneable {
    private CannonVector vector;
    private UUID world;

    public Coordinate(UUID world, double x, double y, double z) {
        this.world = world;
        this.vector = new CannonVector(x, y, z);
    }

    @Override
    public Coordinate clone() {
        try {
            Coordinate clone = (Coordinate) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
