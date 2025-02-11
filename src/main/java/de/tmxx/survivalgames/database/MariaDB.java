package de.tmxx.survivalgames.database;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class MariaDB implements Database {
    private static final String DRIVER_CLASS = "org.mariadb.jdbc.Driver";
    private static final String CONNECTION_URL = "jdbc:mariadb://%s:%d/%s";

    private BasicDataSource dataSource;
    private final Set<Connection> connections = new CopyOnWriteArraySet<>();

    private final String host;
    private final int port;
    private final String database;
    private final String user;
    private final String password;

    public MariaDB(ConfigurationSection section) {
        host = section.getString("host");
        port = section.getInt("port");
        database = section.getString("database");
        user = section.getString("user");
        password = section.getString("password");
    }

    @Override
    public void connect() {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(DRIVER_CLASS);
        dataSource.setUrl(CONNECTION_URL.formatted(host, port, database));
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    @Override
    public void disconnect() {
        if (connections.isEmpty()) return;

        connections.forEach(connection -> {
            try {
                connection.close();
            } catch (SQLException ignored) {}
        });
        connections.clear();
    }

    @Override
    public Connection getConnection() {
        try {
            Connection connection = dataSource.getConnection();
            connections.add(connection);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close(Connection connection) {
        if (connection == null) return;

        try {
            connection.close();
            connections.remove(connection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long update(Connection connection, String sql, Object... args) {
        long generatedKey = DEFAULT_UPDATE_LONG;

        try (PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys != null) {
                while (generatedKeys.next()) generatedKey = generatedKeys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return generatedKey;
    }

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

    @Override
    public Result query(Connection connection, String sql, Object... args) {
        Result result;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            result = Result.from(statement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    @Override
    public Result query(String query, Object... args) {
        if (dataSource == null) return new Result();

        Connection connection = getConnection();
        if (connection == null) return new Result();
        try {
            return query(connection, query, args);
        } finally {
            close(connection);
        }
    }
}
