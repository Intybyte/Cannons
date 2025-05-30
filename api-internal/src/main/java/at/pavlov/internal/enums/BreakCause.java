package at.pavlov.internal.enums;


public enum BreakCause {

    //Error Messages
    PlayerBreak ("player break"),
    Explosion ("explosion"),
    Overheating ("overheating"),
    ShipDestroyed ("ShipDestroyed"),
    Overloading ("overloading"),
    Dismantling ("dismantling"),
    Other ("other");

    private final String str;

    BreakCause(String str)
    {
        this.str = str;
    }

    public String getString()
    {
        return str;
    }
}
