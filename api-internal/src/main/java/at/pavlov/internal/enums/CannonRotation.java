package at.pavlov.internal.enums;

public enum CannonRotation {
    LEFT(-90),
    RIGHT(90),
    FLIP(180);

    private final int angle;
    CannonRotation(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }
}
