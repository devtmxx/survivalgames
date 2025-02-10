package de.tmxx.survivalgames.database;

import java.sql.Connection;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface Database {
    long DEFAULT_UPDATE_LONG = -1;

    void connect();
    void disconnect();
    Connection getConnection();
    void close(Connection connection);
    long update(Connection connection, String sql, Object... args);
    long update(String update, Object... args);
    Result query(Connection connection, String sql, Object... args);
    Result query(String query, Object... args);
}
