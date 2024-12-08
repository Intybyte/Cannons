package at.pavlov.internal.cannons.data;

import lombok.Data;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.UUID;

@Setter(onMethod_ = {@ApiStatus.Internal})
@Data public class SentryData {
    // tracking entity
    private UUID sentryEntity;
    // store older targets, so we do not target the same all the time
    private ArrayList<UUID> sentryEntityHistory;
    // how long this entity is targeted by this cannon
    private long sentryTargetingTime = 0;
    // last time loading was tried and failed. Wait some time before trying again
    private long sentryLastLoadingFailed = 0;
    // last time firing failed. Wait some time before trying again
    private long sentryLastFiringFailed;
    // return to default angles after firing
    private boolean sentryHomedAfterFiring;
    // last time the sentry mode solution was updated
    private long lastSentryUpdate;

    //target options for cannon
    private boolean targetMob;
    private boolean targetPlayer;
    private boolean targetCannon;
    private boolean targetOther;
}
