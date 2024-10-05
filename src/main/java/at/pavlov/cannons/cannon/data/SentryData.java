package at.pavlov.cannons.cannon.data;

import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data public class SentryData {
    // tracking entity
    private UUID sentryEntity;
    // store older targets, so we do not target the same all the time
    private ArrayList<UUID> sentryEntityHistory;
    // how long this entity is targeted by this cannon
    private long sentryTargetingTime;
    // last time loading was tried and failed. Wait some time before trying again
    private long sentryLastLoadingFailed;
    // last time firing failed. Wait some time before trying again
    private long sentryLastFiringFailed;
    // return to default angles after firing
    private boolean sentryHomedAfterFiring;

    //target options for cannon
    private boolean targetMob;
    private boolean targetPlayer;
    private boolean targetCannon;
    private boolean targetOther;
}
