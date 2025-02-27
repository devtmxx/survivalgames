package de.tmxx.survivalgames.stats.database.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.stats.database.Database;
import de.tmxx.survivalgames.stats.database.Result;
import de.tmxx.survivalgames.stats.database.Row;
import de.tmxx.survivalgames.stats.database.UserIDLoader;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class UserIDLoaderImpl implements UserIDLoader {
    private final Database database;
    private final Map<UUID, Integer> uuidMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> nameMap = new ConcurrentHashMap<>();

    @Inject
    UserIDLoaderImpl(Database database) {
        this.database = database;
    }

    @Override
    public int loadOrCreateUserId(UUID uniqueId, String name) {
        int id = getUserIdIfCached(uniqueId);
        if (id != -1) return id;

        Connection connection = database.getConnection();
        try {
            id = loadUserIdFromDatabase(connection, uniqueId, name);
            if (id != -1) {
                cacheUserId(uniqueId, name, id);
                return id;
            }

            id = createUser(connection, uniqueId, name);
            cacheUserId(uniqueId, name, id);
            return id;
        } finally {
            database.close(connection);
        }
    }

    @Override
    public int loadUserId(String name) {
        int id = getUserIdIfCached(name);
        if (id != -1) return id;

        id = loadUserIdFromDatabase(name);
        cacheUserId(null, name, id);

        return id;
    }

    private int getUserIdIfCached(UUID uniqueId) {
        return uuidMap.getOrDefault(uniqueId, -1);
    }

    private int getUserIdIfCached(String name) {
        return nameMap.getOrDefault(name.toLowerCase(), -1);
    }

    private void cacheUserId(@Nullable UUID uniqueId, String name, int id) {
        if (uniqueId != null) uuidMap.put(uniqueId, id);
        nameMap.put(name.toLowerCase(), id);
    }

    private int loadUserIdFromDatabase(Connection connection, UUID uniqueId, String name) {
        Result result = database.query(connection, "SELECT `id`, `name` FROM `users` WHERE `unique_id` = ?", uniqueId.toString());
        if (result.getRows().isEmpty()) return -1;

        Row row = result.getRows().getFirst();
        int id = row.getInt("id");

        if (!row.getString("name").equals(name)) {
            updateUserName(connection, id, name);
        }

        return id;
    }

    private int loadUserIdFromDatabase(String name) {
        Result result = database.query("SELECT `id` FROM `users` WHERE `name` = ?", name);
        if (result.getRows().isEmpty()) return -1;

        Row row = result.getRows().getFirst();
        return row.getInt("id");
    }

    private void updateUserName(Connection connection, int id, String name) {
        database.update(connection, "UPDATE `users` SET `name` = ? WHERE `id` = ?;", name, id);
    }

    private int createUser(Connection connection, UUID uniqueId, String name) {
        return (int) database.update(
                connection,
                "INSERT INTO `users` (`unique_id`, `name`) VALUES (?, ?);",
                uniqueId.toString(),
                name
        );
    }
}
