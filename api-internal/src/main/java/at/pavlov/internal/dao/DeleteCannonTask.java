package at.pavlov.internal.dao;

import at.pavlov.internal.CannonDatabase;
import at.pavlov.internal.async.RunnableAsync;

import java.sql.Statement;
import java.util.UUID;

public class DeleteCannonTask implements RunnableAsync {
    private static final CannonDatabase cannonDatabase = CannonDatabase.getInstance();
    private final UUID cannonId;
    private final UUID playerId;

    public DeleteCannonTask() {
        this.cannonId = null;
        this.playerId = null;
    }

    public DeleteCannonTask(UUID cannonId) {
        this.cannonId = cannonId;
        this.playerId = null;
    }

    public DeleteCannonTask(UUID playerId, boolean player) {
        this.cannonId = null;
        this.playerId = playerId;
    }


    @Override
    public void run() {
        try (Statement statement = cannonDatabase.connection.createStatement()) {
            if (cannonId == null && playerId == null) {
                statement.executeUpdate(String.format("DELETE FROM %s", cannonDatabase.cannonsDb));
            } else if (cannonId != null) {
                statement.executeUpdate(String.format("DELETE FROM %s WHERE id='%s'", cannonDatabase.cannonsDb, cannonId));
            } else {
                statement.executeUpdate(String.format("DELETE FROM %s WHERE owner='%s'", cannonDatabase.cannonsDb, playerId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
