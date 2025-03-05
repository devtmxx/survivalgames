package de.tmxx.survivalgames.stats.database.util;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public record DatabaseCredentials(String host, int port, String database, String username, String password, String tablePrefix) {
    public static DatabaseCredentials standard() {
        return new DatabaseCredentials("localhost", 3306, "survivalgames", "survivalgames", "password", "survivalgames_");
    }

    public static DatabaseCredentials fromConfig(@Nullable ConfigurationSection section) {
        DatabaseCredentials standard = DatabaseCredentials.standard();
        if (section == null) return standard;

        return new DatabaseCredentials(
                section.getString("host", standard.host()),
                section.getInt("port", standard.port()),
                section.getString("database", standard.database()),
                section.getString("user", standard.username()),
                section.getString("password", standard.password()),
                section.getString("table-prefix", standard.tablePrefix())
        );
    }
}
