package at.pavlov.cannons.interfaces;

import java.util.UUID;

//TODO: move to holder component
public interface ISentry {
    void setTargetMob(boolean target);
    void setTargetPlayer(boolean target);
    void setTargetCannon(boolean target);
    void setTargetOther(boolean target);

    boolean isTargetMob();
    boolean isTargetPlayer();
    boolean isTargetCannon();
    boolean isTargetOther();

    UUID getSentryEntity();
    default boolean hasSentryEntity() {
        return getSentryEntity() != null;
    }
    void setSentryTarget(UUID sentryTarget);

}
