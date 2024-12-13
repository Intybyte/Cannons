package at.pavlov.internal.container.location;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data public class CannonVector implements Cloneable {
    private double x = 0.0, y = 0.0, z = 0.0;

    @Override
    public CannonVector clone() {
        try {
            return (CannonVector) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
