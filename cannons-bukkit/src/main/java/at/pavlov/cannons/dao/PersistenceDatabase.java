package at.pavlov.cannons.dao;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.cannon.Cannon;
import at.pavlov.cannons.cannon.CannonManager;

import java.util.UUID;
import java.util.concurrent.Future;

public class PersistenceDatabase {
    private final Cannons plugin;
    private Future<?> saveTask = null;

    public PersistenceDatabase(Cannons plugin) {
        this.plugin = plugin;
    }

    public void createTables() {
        CreateTableTask createTableTask = new CreateTableTask();
        createTableTask.run();
    }

    /**
     * loads all cannons from the database
     *
     * @return true if loading was successful
     */
    public void loadCannons() {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        CannonManager.getInstance().clearCannonList();

        LoadCannonTask task = new LoadCannonTask();
        task.runTaskAsynchronously();
    }

    public void saveAllCannons(boolean async) {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        SaveCannonTask saveCannonTask = new SaveCannonTask();
        if (async) {
            saveTask = saveCannonTask.runTaskAsynchronously();
        } else {
            saveCannonTask.run();
        }
    }

    public void saveCannon(Cannon cannon) {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        SaveCannonTask saveCannonTask = new SaveCannonTask(cannon.getUID());
        saveTask = saveCannonTask.runTaskAsynchronously();
    }

    public boolean isSaveTaskRunning() {
        return saveTask != null && !saveTask.isDone();
    }

    public void deleteCannon(UUID cannon_id) {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        DeleteCannonTask deleteCannonTask = new DeleteCannonTask(cannon_id);
        deleteCannonTask.runTaskAsynchronously();
    }

    public void deleteAllCannons() {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        DeleteCannonTask deleteCannonTask = new DeleteCannonTask();
        deleteCannonTask.runTaskAsynchronously();
    }

    public void deleteCannons(UUID player_id) {
        if (!plugin.hasConnection()) {
            plugin.logSevere("No connection to database");
            return;
        }
        DeleteCannonTask deleteCannonTask = new DeleteCannonTask(player_id, true);
        deleteCannonTask.runTaskAsynchronously();
    }
}
