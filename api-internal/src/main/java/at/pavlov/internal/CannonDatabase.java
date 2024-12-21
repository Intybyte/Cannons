package at.pavlov.internal;

import java.sql.Connection;

public interface CannonDatabase {
    String getWhitelistDatabase();
    String getCannonDatabase();
    Connection getConnection();
}
