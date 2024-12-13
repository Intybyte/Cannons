package at.pavlov.internal.enums;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum EntityDataType {
    FUSE_TIME ("Fuse"),
    POTION_EFFECT ("Potion"),
    COLOR ("Color"),
    PARTICLE ("Particle"),
    REAPPLICATION_DELAY ("ReapplicationDelay"),
    RADIUS ("Radius"),
    RADIUS_PER_TICK ("RadiusPerTick"),
    RADIUS_ON_USE ("RadiusOnUse"),
    DURATION ("Duration"),
    DURATION_ON_USE ("DurationOnUse"),
    WAIT_TIME ("WaitTime"),
    MAIN_HAND_ITEM ("MainHandItem"),
    OFF_HAND_ITEM ("OffHandItem"),
    HELMET_ARMOR_ITEM ("HelmetArmorItem"),
    CHESTPLATE_ARMOR_ITEM("ChestplateArmorItem"),
    LEGGINGS_ARMOR_ITEM("LeggingsArmorItem"),
    BOOTS_ARMOR_ITEM ("BootsArmorItem");

    private final String str;

    EntityDataType(String str)
    {
        this.str = str;
    }

    public String getString()
    {
        return str;
    }

    private static final Map<String, EntityDataType> LOOKUP_MAP;

    static {
        LOOKUP_MAP = new HashMap<>();
        for (EntityDataType dataType : values()) {
            LOOKUP_MAP.put(dataType.getString().toLowerCase(), dataType);
        }
    }

    public static EntityDataType lookup(String str) {
        String lookup = str.toLowerCase();
        return LOOKUP_MAP.get(lookup);
    }
}
