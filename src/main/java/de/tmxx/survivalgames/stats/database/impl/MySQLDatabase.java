package de.tmxx.survivalgames.stats.database.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.module.game.PluginLogger;
import de.tmxx.survivalgames.stats.database.Database;
import de.tmxx.survivalgames.stats.database.Result;
import de.tmxx.survivalgames.stats.database.util.DatabaseCredentials;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     Builds connections to a mariadb database.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class MySQLDatabase implements Database {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    private static final String CONNECTION_URL = "jdbc:mysql://%s:%d/%s";
    private static final String TABLE_PREFIX_VARIABLE = "%table_prefix%";

    private BasicDataSource dataSource;
    private final Set<Connection> connections = new CopyOnWriteArraySet<>();

    private final Logger logger;
    private final DatabaseCredentials credentials;

    @Inject
    MySQLDatabase(@PluginLogger Logger logger, DatabaseCredentials credentials) {
        this.logger = logger;
        this.credentials = credentials;
    }

    @Override
    public String replaceTablePrefix(String input) {
        return input.replaceAll(TABLE_PREFIX_VARIABLE, credentials.tablePrefix());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connect() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(CONNECTION_URL.formatted(credentials.host(), credentials.port(), credentials.database()));
        dataSource.setUsername(credentials.username());
        dataSource.setPassword(credentials.password());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disconnect() {
        logger.info("[Database] Closing all database connections...");
        if (connections.isEmpty()) return;

        connections.forEach(connection -> {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        });
        connections.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            connections.add(connection);
            return connection;
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while opening connection", e);
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close(Connection connection) {
        if (connection == null) return;

        try {
            connection.close();
            connections.remove(connection);
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while closing connection", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long update(Connection connection, String sql, Object... args) {
        long generatedKey = DEFAULT_UPDATE_LONG;

        try (PreparedStatement statement = connection.prepareStatement(replaceTablePrefix(sql), PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys != null) {
                while (generatedKeys.next()) generatedKey = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while executing update", e);
        }

        return generatedKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long update(String update, Object... args) {
        long generatedKey = DEFAULT_UPDATE_LONG;
        if (dataSource == null) return generatedKey;

        Connection connection = getConnection();
        if (connection == null) return generatedKey;
        try {
            return update(connection, update, args);
        } finally {
            close(connection);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Result query(Connection connection, String sql, Object... args) {
        try (PreparedStatement statement = connection.prepareStatement(replaceTablePrefix(sql))) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            return Result.from(statement.executeQuery());
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while executing query", e);
        }

        return Result.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NotNull Result query(String query, Object... args) {
        if (dataSource == null) return Result.empty();

        Connection connection = getConnection();
        if (connection == null) return Result.empty();
        try {
            return query(connection, query, args);
        } finally {
            close(connection);
        }
    }
}
