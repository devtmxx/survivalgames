package de.tmxx.survivalgames;

import de.tmxx.survivalgames.auto.AutoRegistrar;
import de.tmxx.survivalgames.database.Database;
import de.tmxx.survivalgames.database.MariaDB;
import de.tmxx.survivalgames.i18n.I18n;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Getter
public class SurvivalGames extends JavaPlugin {
    private static final int CONFIG_VERSION = 1;

    private I18n i18n;
    private Database database;

    @Override
    public void onEnable() {
        registerConfigurationSerializables();
        loadConfig();

        i18n = new I18n(this);

        setupDatabase();

        AutoRegistrar.registerListeners(this);
        AutoRegistrar.registerCommands(this);
    }

    public boolean isSetup() {
        return getConfig().getBoolean("setup");
    }

    private void setupDatabase() {
        database = getConfig().getSerializable("database", MariaDB.class);
        if (database == null) {
            getLogger().info("Cannot find database configuration");
            return;
        }
        database.connect();
        getLogger().info("Ready for database connections");
    }

    private void registerConfigurationSerializables() {
        ConfigurationSerialization.registerClass(MariaDB.class);
    }

    private void loadConfig() {
        saveDefaultConfig();
        if (getConfig().getInt("version") >= CONFIG_VERSION) return;

        updateConfig();
    }

    private void updateConfig() {
        FileConfiguration config = getConfig();

        saveResource("config.yml", true);
        reloadConfig();

        FileConfiguration newConfig = getConfig();
        config.getKeys(true).forEach(key -> newConfig.set(key, config.get(key)));
        newConfig.set("version", CONFIG_VERSION);

        saveConfig();
    }
}
