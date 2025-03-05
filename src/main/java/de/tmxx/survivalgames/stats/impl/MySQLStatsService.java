package de.tmxx.survivalgames.stats.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.module.game.PluginLogger;
import de.tmxx.survivalgames.stats.Stats;
import de.tmxx.survivalgames.stats.StatsKey;
import de.tmxx.survivalgames.stats.StatsService;
import de.tmxx.survivalgames.stats.database.Database;
import de.tmxx.survivalgames.stats.database.Result;
import de.tmxx.survivalgames.stats.database.UserIDLoader;
import de.tmxx.survivalgames.stats.database.util.DatabaseCredentials;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class MySQLStatsService implements StatsService {
    private final Logger logger;
    private final Database database;
    private final DatabaseCredentials credentials;
    private final UserIDLoader userIDLoader;
    private final Map<StatsKey, Integer> STAT_KEY_IDS = new ConcurrentHashMap<>();

    @Inject
    MySQLStatsService(@PluginLogger Logger logger, Database database, DatabaseCredentials credentials, UserIDLoader userIDLoader) {
        this.logger = logger;
        this.database = database;
        this.credentials = credentials;
        this.userIDLoader = userIDLoader;
    }

    @Override
    public void prepare() {
        Connection connection = database.getConnection();
        try {
            List<String> existingTables = getExistingTables(connection);

            createUsersTableIfNotExists(connection, existingTables);
            createStatsKeysTableIfNotExists(connection, existingTables);
            createStatsTableIfNotExists(connection, existingTables);

            loadExistingKeyIds(connection);
            createNonExistingKeyIds(connection);
        } finally {
            database.close(connection);
        }
    }

    @Override
    public Stats loadStats(OfflinePlayer player) {
        if (UUID_MAP.containsKey(player.getUniqueId())) return UUID_MAP.get(player.getUniqueId());

        int userId = userIDLoader.loadOrCreateUserId(player.getUniqueId(), player.getName());
        Stats stats = loadStatsByUserId(userId);

        UUID_MAP.put(player.getUniqueId(), stats);
        if (player.getName() != null) NAME_MAP.put(player.getName().toLowerCase(), stats);

        return stats;
    }

    @Override
    public Stats loadStats(String name) {
        if (NAME_MAP.containsKey(name)) return NAME_MAP.get(name);

        int userId = userIDLoader.loadUserId(name);
        Stats stats = loadStatsByUserId(userId);

        NAME_MAP.put(name.toLowerCase(), stats);

        return stats;
    }

    @Override
    public void persist(OfflinePlayer player, Stats stats) {
        int userId = userIDLoader.loadOrCreateUserId(player.getUniqueId(), player.getName());

        Connection connection = database.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(database.replaceTablePrefix("INSERT INTO `%table_prefix%stats` (`user_id`, `key_id`, `value`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `value` = ?;"));
            for (StatsKey key : StatsKey.values()) {
                statement.setInt(1, userId);
                statement.setInt(2, STAT_KEY_IDS.get(key));

                int value = stats.get(key);
                statement.setInt(3, value);
                statement.setInt(4, value);
                statement.addBatch();
            }

            statement.executeBatch();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error while persisting user stats", e);
        } finally {
            database.close(connection);
        }
    }

    private void loadExistingKeyIds(Connection connection) {
        Result result = database.query(connection, "SELECT `id`, `name` FROM `%table_prefix%stats_keys`;");
        result.getRows().forEach(row -> {
            StatsKey key = StatsKey.getByKey(row.getString("name"));
            if (key == null) return;

            STAT_KEY_IDS.put(key, row.getInt("id"));
        });
    }

    private void createNonExistingKeyIds(Connection connection) {
        for (StatsKey key : StatsKey.values()) {
            if (STAT_KEY_IDS.containsKey(key)) continue;

            int id = (int) database.update(connection, "INSERT INTO `%table_prefix%stats_keys` (`name`) VALUES (?);", key.getStorageKey());
            STAT_KEY_IDS.put(key, id);
        }
    }

    private Stats loadStatsByUserId(int userId) {
        Result result = database.query(
                "SELECT `%table_prefix%stats_keys`.`name`, `%table_prefix%stats`.`value` FROM `%table_prefix%stats` " +
                        "LEFT JOIN `%table_prefix%stats_keys` ON `key_id` = `%table_prefix%stats_keys`.`id` WHERE `user_id` = ?;",
                userId
        );

        Stats stats = new Stats();
        result.getRows().forEach(row -> {
            StatsKey key = StatsKey.getByKey(row.getString("name"));
            if (key == null) return;

            stats.add(key, row.getInt("value"));
        });
        return stats;
    }

    private List<String> getExistingTables(Connection connection) {
        Result result = database.query(
                connection,
                "SELECT `table_name` FROM `information_schema`.`tables` WHERE `table_schema` = ?",
                credentials.database()
        );

        return result.getRows().stream().map(row -> row.getString("TABLE_NAME")).toList();
    }

    private void createUsersTableIfNotExists(Connection connection, List<String> existingTables) {
        if (existingTables.contains(database.replaceTablePrefix("%table_prefix%users"))) return;

        database.update(
                connection,
                "CREATE TABLE `%table_prefix%users` (" +
                        "`id` INT NOT NULL AUTO_INCREMENT, `" +
                        "unique_id` VARCHAR(36) NOT NULL, " +
                        "`name` VARCHAR(17) NOT NULL, " +
                        "PRIMARY KEY(`id`), " +
                        "UNIQUE (`unique_id`));"
        );
    }

    private void createStatsKeysTableIfNotExists(Connection connection, List<String> existingTables) {
        if (existingTables.contains(database.replaceTablePrefix("%table_prefix%stats_keys"))) return;

        database.update(
                connection,
                "CREATE TABLE `%table_prefix%stats_keys` (" +
                        "`id` INT NOT NULL AUTO_INCREMENT, " +
                        "`name` VARCHAR(16) NOT NULL, " +
                        "PRIMARY KEY(`id`));"
        );
    }

    private void createStatsTableIfNotExists(Connection connection, List<String> existingTables) {
        if (existingTables.contains(database.replaceTablePrefix("%table_prefix%stats"))) return;

        database.update(
                connection,
                "CREATE TABLE `%table_prefix%stats` (" +
                        "`id` INT NOT NULL AUTO_INCREMENT, " +
                        "`user_id` INT NOT NULL, " +
                        "`key_id` INT NOT NULL, " +
                        "`value` INT NOT NULL, " +
                        "PRIMARY KEY(`id`), " +
                        "UNIQUE KEY(`user_id`, `key_id`), " +
                        "FOREIGN KEY(`key_id`) REFERENCES `%table_prefix%stats_keys`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT, " +
                        "FOREIGN KEY(`user_id`) REFERENCES `%table_prefix%users`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT);"
        );
    }
}
