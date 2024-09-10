package at.pavlov.cannons.interfaces;

import java.util.UUID;

public interface ITurret {
    void setTargetMob(boolean target);
    void setTargetPlayer(boolean target);
    void setTargetCannon(boolean target);
    void setTargetOther(boolean target);

    boolean isTargetMob();
    boolean isTargetPlayer();
    boolean isTargetCannon();
    boolean isTargetOther();

    double getAimingPitch();
    void setAimingPitch(double aimingPitch);
    double getAimingYaw();
    void setAimingYaw(double aimingYaw);

    UUID getSentryEntity();
    default boolean hasSentryEntity() {
        return getSentryEntity() != null;
    }
    void setSentryTarget(UUID sentryTarget);

}
