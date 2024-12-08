package at.pavlov.internal.cannons.functionalities;

import at.pavlov.internal.enums.CannonRotation;

public interface Rotational<Vector> {

    void rotate(Vector center, CannonRotation rotation);
    /**
     * updates the rotation of the cannon by rotating it 90 to the right
     *
     * @param center - center of the rotation
     */
    default void rotateRight(Vector center) {
        this.rotate(center, CannonRotation.RIGHT);
    }

    /**
     * updates the rotation of the cannon by rotating it 90 to the left
     *
     * @param center - center of the rotation
     */
    default void rotateLeft(Vector center) {
        this.rotate(center, CannonRotation.LEFT);
    }

    /**
     * updates the rotation of the cannon by rotating it 180
     *
     * @param center - center of the rotation
     */
    default void rotateFlip(Vector center) {
        this.rotate(center, CannonRotation.FLIP);
    }
}
