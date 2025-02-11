package de.tmxx.survivalgames;

import de.tmxx.survivalgames.auto.AutoRegistrar;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.database.Database;
import de.tmxx.survivalgames.database.MariaDB;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.map.MapManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
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
    private MapManager mapManager;

    @Override
    public void onEnable() {
        registerConfigurationSerializables();
        loadConfig();

        i18n = new I18n(this);

        setupDatabase();

        mapManager = new MapManager(this);
        mapManager.load();

        AutoRegistrar.registerListeners(this);
        AutoRegistrar.registerCommands(this);
    }

    public boolean isSetup() {
        return getConfig().getBoolean("setup");
    }

    public int getMinPlayers() {
        return getConfig().getInt("players.min");
    }

    public int getMaxPlayers() {
        return getConfig().getInt("players.max");
    }

    private void setupDatabase() {
        ConfigurationSection section = getConfig().getConfigurationSection("database");
        if (section == null) {
            getLogger().info("Cannot find database configuration");
            return;
        }

        database = new MariaDB(section);
        database.connect();
        getLogger().info("Ready for database connections");
    }

    private void registerConfigurationSerializables() {
        ConfigurationSerialization.registerClass(SpawnPosition.class);
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
