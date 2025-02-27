package de.tmxx.survivalgames.stats.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.module.config.DataFolder;
import de.tmxx.survivalgames.module.game.PluginLogger;
import de.tmxx.survivalgames.stats.Stats;
import de.tmxx.survivalgames.stats.StatsKey;
import de.tmxx.survivalgames.stats.StatsService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
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
public class ConfigStatsService implements StatsService {
    private final File directory;
    private final Logger logger;

    @Inject
    ConfigStatsService(@DataFolder File dataFolder, @PluginLogger Logger logger) {
        directory = new File(dataFolder, "stats");
        this.logger = logger;
    }

    @Override
    public void prepare() {
        if (!directory.exists() && !directory.mkdirs()) {
            logger.severe("Failed to create stats directory");
        }
    }

    @Override
    public Stats loadStats(OfflinePlayer player) {
        if (player == null) return null;

        File statsFile = new File(directory, player.getUniqueId() + ".yml");
        if (!statsFile.exists()) return new Stats();

        FileConfiguration config = YamlConfiguration.loadConfiguration(statsFile);
        Stats stats = loadStatsFromConfig(config);
        UUID_MAP.put(player.getUniqueId(), stats);

        return stats;
    }

    @Override
    public Stats loadStats(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
        if (player == null) return null;

        return loadStats(player);
    }

    @Override
    public void persist(OfflinePlayer player, Stats stats) {
        File statsFile = new File(directory, player.getUniqueId() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        for (StatsKey key : StatsKey.values()) {
            config.set(key.getStorageKey(), stats.get(key));
        }

        try {
            config.save(statsFile);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save user stats to file", e);
        }
    }

    private Stats loadStatsFromConfig(FileConfiguration config) {
        Stats stats = new Stats();
        for (StatsKey key : StatsKey.values()) {
            stats.add(key, config.getInt(key.getStorageKey(), 0));
        }
        return stats;
    }
}
