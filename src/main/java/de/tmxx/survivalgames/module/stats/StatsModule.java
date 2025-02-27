package de.tmxx.survivalgames.module.stats;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.stats.StatsService;
import de.tmxx.survivalgames.stats.database.Database;
import de.tmxx.survivalgames.stats.database.impl.MySQLDatabase;
import de.tmxx.survivalgames.stats.database.util.DatabaseCredentials;
import de.tmxx.survivalgames.stats.impl.ConfigStatsService;
import de.tmxx.survivalgames.stats.impl.MySQLStatsService;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class StatsModule extends AbstractModule {
    @Override
    protected void configure() {
        // binding directly to the mysql database as long as no other database is supported
        bind(Database.class).to(MySQLDatabase.class);
    }

    @Provides
    @Singleton
    StorageType provideStorageType(@MainConfig FileConfiguration config) {
        try {
            return StorageType.valueOf(config.getString("storage-type", "config").toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return StorageType.CONFIG;
        }
    }

    @Provides
    @Singleton
    DatabaseCredentials provideDatabaseCredentials(@MainConfig FileConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection("database");
        return DatabaseCredentials.fromConfig(section);
    }

    @Provides
    @Singleton
    StatsService provideStatsService(StorageType type, ConfigStatsService configStatsService, MySQLStatsService mysqlStatsService) {
        return switch (type) {
            case CONFIG -> configStatsService;
            case MYSQL -> mysqlStatsService;
        };
    }

    enum StorageType {
        CONFIG,
        MYSQL
    }
}
