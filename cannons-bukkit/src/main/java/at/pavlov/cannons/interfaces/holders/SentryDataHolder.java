package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.data.SentryData;
import at.pavlov.cannons.interfaces.functionalities.Updatable;

import java.util.UUID;

public interface SentryDataHolder extends Updatable {
    SentryData getSentryData();
    void setSentryData(SentryData sentryData);

    //region Sentry Entity
    default UUID getSentryEntity() {
        return getSentryData().getSentryEntity();
    }

    default boolean hasSentryEntity() {
        return getSentryEntity() != null;
    }

    /**
     * Set this as new sentry target and add it to the list of targeted entities
     *
     * @param sentryTarget
     */
    default void setSentryEntity(UUID sentryTarget) {
        getSentryData().setSentryEntity(sentryTarget);
        if (sentryTarget == null) {
            return;
        }
        setSentryTargetingTime(System.currentTimeMillis());
        var history = getSentryData().getSentryEntityHistory();
        //store only 5
        if (history.size() > 5)
            history.remove(0);
        history.add(sentryTarget);
    }
    //endregion

    //region Sentry Timestamps
    default long getSentryTargetingTime() {
        return getSentryData().getSentryTargetingTime();
    }

    default void setSentryTargetingTime(long sentryTargetingTime) {
        getSentryData().setSentryTargetingTime(sentryTargetingTime);
    }

    default long getSentryLastLoadingFailed() {
        return getSentryData().getSentryLastLoadingFailed();
    }

    default void setSentryLastLoadingFailed(long sentryLastLoadingFailed) {
        getSentryData().setSentryLastLoadingFailed(sentryLastLoadingFailed);
    }

    default long getSentryLastFiringFailed() {
        return getSentryData().getSentryLastFiringFailed();
    }

    default void setSentryLastFiringFailed(long sentryLastFiringFailed) {
        getSentryData().setSentryLastFiringFailed(sentryLastFiringFailed);
    }

    default long getLastSentryUpdate() {
        return getSentryData().getLastSentryUpdate();
    }

    default void setLastSentryUpdate(long lastSentryUpdate) {
        getSentryData().setLastSentryUpdate(lastSentryUpdate);
    }
    //endregion
    
    //region Target Handler
    default boolean isTargetMob() {
        return getSentryData().isTargetMob();
    }

    default void setTargetMob(boolean targetMob) {
        this.getSentryData().setTargetMob(targetMob);
        this.hasUpdated();
    }

    default void toggleTargetMob() {
        setTargetMob(!this.isTargetMob());
    }

    default boolean isTargetPlayer() {
        return getSentryData().isTargetPlayer();
    }

    default void setTargetPlayer(boolean targetPlayer) {
        this.getSentryData().setTargetPlayer(targetPlayer);
        this.hasUpdated();
    }

    default void toggleTargetPlayer() {
        setTargetPlayer(!this.isTargetPlayer());
    }

    default boolean isTargetCannon() {
        return getSentryData().isTargetCannon();
    }

    default void setTargetCannon(boolean targetCannon) {
        this.getSentryData().setTargetCannon(targetCannon);
        this.hasUpdated();
    }

    default void toggleTargetCannon() {
        setTargetCannon(!this.isTargetCannon());
    }

    default boolean isTargetOther() {
        return getSentryData().isTargetOther();
    }

    default void setTargetOther(boolean targetOther) {
        this.getSentryData().setTargetOther(targetOther);
        this.hasUpdated();
    }

    default void toggleTargetOther() {
        setTargetOther(!this.isTargetOther());
    }
    //endregion

    default boolean isSentryHomedAfterFiring() {
        return getSentryData().isSentryHomedAfterFiring();
    }

    default void setSentryHomedAfterFiring(boolean sentryHomedAfterFiring) {
        getSentryData().setSentryHomedAfterFiring(sentryHomedAfterFiring);
    }

    /**
     * was this entity targeted in the last time
     *
     * @param entityId ID of the entity
     * @return true if it was target
     */
    default boolean wasSentryTarget(UUID entityId) {
        return entityId != null && getSentryData().getSentryEntityHistory().contains(entityId);
    }
}
