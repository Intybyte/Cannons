package at.pavlov.internal;

import lombok.Getter;

import java.sql.Connection;

public class CannonDatabase {
    @Getter
    private static CannonDatabase instance = null;
    public final Connection connection;
    public final String whitelistDb;
    public final String cannonsDb;

    private CannonDatabase(Connection connection, String whitelistDb, String cannonsDb) {
        this.connection = connection;
        this.whitelistDb = whitelistDb;
        this.cannonsDb = cannonsDb;
    }

    public static void instantiate(Connection connection, String whitelistDb, String cannonsDb) {
        if (CannonDatabase.instance == null) {
            CannonDatabase.instance = new CannonDatabase(connection, whitelistDb, cannonsDb);
        }
    }
}
