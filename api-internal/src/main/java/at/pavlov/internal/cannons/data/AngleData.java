package at.pavlov.internal.cannons.data;

import lombok.Data;

@Data public class AngleData {
    private double horizontalAngle;
    private double verticalAngle;
    // additional angle if the cannon is mounted e.g. a ship which is facing a different angle
    private double additionalHorizontalAngle;
    private double additionalVerticalAngle;
}
