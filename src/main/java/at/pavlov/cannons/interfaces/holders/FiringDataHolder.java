package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.data.FiringData;
import at.pavlov.cannons.interfaces.Updatable;
import at.pavlov.cannons.projectile.Projectile;

import java.util.UUID;

//TODO: Separate all "Last" to a cannon state class if this gets too convoluted
interface FiringDataHolder extends Updatable {
    FiringData getFiringData();
    void setFiringData(FiringData firingData);

    default Projectile getLastFiredProjectile() {
        return getFiringData().getLastFiredProjectile();
    }
    default int getLastFiredGunpowder() {
        return getFiringData().getLastFiredGunpowder();
    }

    //region Fired Cannonballs
    default long getFiredCannonballs() {
        return getFiringData().getFiredCannonballs();
    }

    default void setFiredCannonballs(long firedCannonballs) {
        getFiringData().setFiredCannonballs(firedCannonballs);
        this.hasUpdated();
    }

    default void incrementFiredCannonballs() {
        this.setFiredCannonballs(this.getFiredCannonballs() + 1);
        this.hasUpdated();
    }
    //endregion

    default long getLastFired() {
        return getFiringData().getLastFired();
    }

    default void setLastFired(long lastFired) {
        this.getFiringData().setLastFired(lastFired);
        this.hasUpdated();
    }

    default long getLastLoaded() {
        return getFiringData().getLastLoaded();
    }

    default void setLastLoaded(long lastLoaded) {
        getFiringData().setLastLoaded(lastLoaded);
    }

    default UUID getLastUser() {
        return getFiringData().getLastUser();
    }
    void setLastUser(UUID lastUser);

    boolean isFiring();

    boolean finishedFiringAndLoading();

    default void setFiring() {
        getFiringData().setLastIgnited(System.currentTimeMillis());
        this.hasUpdated();
    }

    default long getLastIgnited() {
        return getFiringData().getLastIgnited();
    }
}
