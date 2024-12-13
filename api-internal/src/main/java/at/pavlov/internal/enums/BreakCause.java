package at.pavlov.internal.enums;


public enum BreakCause {

    //Error Messages
    PLAYER_BREAK("player break"),
    EXPLOSION("explosion"),
    OVERHEATING("overheating"),
    SHIP_DESTROYED("ShipDestroyed"),
    OVERLOADING("overloading"),
    DISMANTLING("dismantling"),
    OTHER("other");



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
