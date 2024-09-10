package at.pavlov.cannons.Enum;

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
