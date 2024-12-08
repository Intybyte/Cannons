package at.pavlov.internal.cannons.holders;

import at.pavlov.internal.cannons.data.AmmoLoadingData;
import at.pavlov.internal.cannons.functionalities.Updatable;
import at.pavlov.internal.enums.MessageEnum;

public interface AmmoLoadingDataHolder<Player, Projectile> extends Updatable {
    AmmoLoadingData<Projectile> getAmmoLoadingData();
    void setAmmoLoadingData(AmmoLoadingData<Projectile> ammoLoadingData);

    //region Soot
    default void setSoot(double soot) {
        getAmmoLoadingData().setSoot(soot);
        this.hasUpdated();
    }
    default double getSoot() {
        return getAmmoLoadingData().getSoot();
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

    //region Gunpowder
    default void setLoadedGunpowder(int gunpowder) {
        getAmmoLoadingData().setLoadedGunpowder(gunpowder);
        this.hasUpdated();
    }
    default int getLoadedGunpowder() {
        return getAmmoLoadingData().getLoadedGunpowder();
    }
    boolean isGunpowderLoaded();

    boolean isLoading();
    //endregion

    //region Loaded Projectile
    default void setLoadedProjectile(Projectile projectile) {
        getAmmoLoadingData().setLoadedProjectile(projectile);
        this.hasUpdated();
    }
    default Projectile getLoadedProjectile() {
        return getAmmoLoadingData().getLoadedProjectile();
    }
    /**
     * is the cannon loaded with a projectile
     *
     * @return - true if there is a projectile in the cannon
     */
    default boolean isProjectileLoaded() {
        return (getLoadedProjectile() != null);
    }
    //endregion

    //region Pushed Projectile
    default void setProjectilePushed(int pushed) {
        getAmmoLoadingData().setProjectilePushed(pushed);
        this.hasUpdated();
    }
    default int getProjectilePushed() {
        return getAmmoLoadingData().getProjectilePushed();
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

    //region Temperature

    // the tempTimestamp set and getters were fully exposed
    // but there should be no reason to directly edit those
    default long getTemperatureTimeStamp() {
        return getAmmoLoadingData().getTempTimestamp();
    }

    default void setTemperatureTimeStamp(long timestamp) {
        hasUpdated();
        getAmmoLoadingData().setTempTimestamp(timestamp);
    }

    /**
     * Calculates the temperature and updates its value
     *
     * @return cannon temperature
     */
    double getTemperature();

    default double getTemperature(boolean update) {
        return (update ? this.getTemperature() : this.getAmmoLoadingData().getTempValue());
    }

    boolean barrelTooHot();
    boolean isOverheatedAfterFiring();
    boolean automaticCooling();
    //endregion

    /**
     * is cannon loaded return true
     *
     * @return - true if the cannon is loaded with a projectile and gunpowder
     */
    default boolean isLoaded() {
        return isProjectileLoaded() && isGunpowderLoaded() && !isLoading();
    }

    MessageEnum loadProjectile(Projectile projectile, Player player);
    MessageEnum loadGunpowder(Player player);
}
