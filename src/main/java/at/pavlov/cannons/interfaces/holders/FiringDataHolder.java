package at.pavlov.cannons.interfaces.holders;

import at.pavlov.cannons.cannon.data.FiringData;
import at.pavlov.cannons.interfaces.Updatable;
import at.pavlov.cannons.projectile.Projectile;

//TODO: Separate all "Last" to a cannon state class if this gets too convoluted
interface FiringDataHolder extends Updatable {
    FiringData getFiringData();
    void setFiringData(FiringData firingData);

    //region Soot
    default void setSoot(double soot) {
        getFiringData().setSoot(soot);
        this.hasUpdated();
    }
    default double getSoot() {
        return getFiringData().getSoot();
    }

    default boolean isClean() {
        return getSoot() < 1;
    }

    /**
     * reduces the soot of the cannon by the given amount
     *
     * @param amount soot to reduce
     */
    default void cleanCannon(int amount) {
        setSoot(getSoot() - amount);
    }
    //endregion

    default void setLoadedGunpowder(int gunpowder) {
        getFiringData().setLoadedGunpowder(gunpowder);
        this.hasUpdated();
    }
    default int getLoadedGunpowder() {
        return getFiringData().getLoadedGunpowder();
    }
    public boolean isGunpowderLoaded();

    default void setLoadedProjectile(Projectile projectile) {
        getFiringData().setLoadedProjectile(projectile);
        this.hasUpdated();
    }
    default Projectile getLoadedProjectile() {
        return getFiringData().getLoadedProjectile();
    }
    /**
     * is the cannon loaded with a projectile
     *
     * @return - true if there is a projectile in the cannon
     */
    default boolean isProjectileLoaded() {
        return (getLoadedProjectile() != null);
    }

    default Projectile getLastFiredProjectile() {
        return getFiringData().getLastFiredProjectile();
    }
    default int getLastFiredGunpowder() {
        return getFiringData().getLastFiredGunpowder();
    }

    //region Pushed Projectile
    default void setProjectilePushed(int pushed) {
        getFiringData().setProjectilePushed(pushed);
        this.hasUpdated();
    }
    default int getProjectilePushed() {
        return getFiringData().getProjectilePushed();
    }

    /**
     * is the Projectile in place and done
     *
     * @return if the projectile is ready to fire
     */
    default boolean isProjectilePushed() {
        return (getProjectilePushed() == 0);
    }

    /**
     * pushes the projectile to the gunpowder
     *
     * @param amount how often the projectile is pushed
     */
    default void pushProjectile(int amount) {
        setProjectilePushed(getProjectilePushed() - amount);
    }
    //endregion

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

}
