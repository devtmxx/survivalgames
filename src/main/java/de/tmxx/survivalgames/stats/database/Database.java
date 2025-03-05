package de.tmxx.survivalgames.stats.database;

import de.tmxx.survivalgames.stats.database.util.DatabaseCredentials;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     A basis for all classes building connections to databases.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface Database {
    // the default value returned for database updates
    long DEFAULT_UPDATE_LONG = -1;

    String replaceTablePrefix(String input);

    /**
     * Connects to the underlying database.
     */
    void connect();

    /**
     * Closes all open connections to the underlying database.
     */
    void disconnect();

    /**
     * Open a connection to the underlying database.
     *
     * @return the connection
     */
    Connection getConnection();

    /**
     * Close a previously opened connection to the underlying database.
     *
     * @param connection the connection to close
     */
    void close(Connection connection);

    /**
     * Executes an updated on against the underlying database using the specific connection.
     *
     * @param connection the connection
     * @param sql the update
     * @param args arguments used in the update
     * @return generated keys or {@link #DEFAULT_UPDATE_LONG}
     */
    long update(Connection connection, String sql, Object... args);

    /**
     * Executes an update against the underlying database. This will open a single connection and closes it after the
     * update has been executed. Use {@link #update(Connection, String, Object...)} to execute multiple updates in order
     * to save resources.
     *
     * @param update the update to execute
     * @param args arguments used in the update
     * @return generated keys or {@link #DEFAULT_UPDATE_LONG}
     */
    long update(String update, Object... args);

    /**
     * Queries data from the underlying database using the specific connection.
     *
     * @param connection the connection
     * @param sql the query
     * @param args arguments used in the query
     * @return the result
     */
    @NotNull Result query(Connection connection, String sql, Object... args);

    /**
     * Queries data from the underlying database using. This will open a single connection and closes it after the
     * query has been executed. Use {@link #query(Connection, String, Object...)} to execute multiple queries in order
     * to save resources.
     *
     * @param query the query to execute
     * @param args arguments used in the query
     * @return the result
     */
    @NotNull Result query(String query, Object... args);
}
