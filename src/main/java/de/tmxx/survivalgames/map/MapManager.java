package de.tmxx.survivalgames.map;

import de.tmxx.survivalgames.SurvivalGames;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class MapManager {
    private final SurvivalGames plugin;
    private final File mapsDirectory;
    private final java.util.Map<String, Map> maps = new HashMap<>();

    public MapManager(SurvivalGames plugin) {
        this.plugin = plugin;
        File mapsDirectory = new File(plugin.getDataFolder(), "maps");

        if (!mapsDirectory.exists() && !mapsDirectory.mkdirs()) {
            plugin.getLogger().severe("Could not create maps directory");
            mapsDirectory = null;
        }

        this.mapsDirectory = mapsDirectory;
    }

    public void load() {
        if (mapsDirectory == null) return;

        File[] files = mapsDirectory.listFiles();
        if (files == null) return;

        for (File file : files) {
            Map map = new Map(plugin, file);
            map.load();
            maps.put(map.getId(), map);

            plugin.getLogger().info("Loaded map data for " + map.getId());
        }
    }

    public @Nullable Map create(String id) {
        if (mapsDirectory == null) {
            plugin.getLogger().warning("Cannot create map data if the maps directory is not accessible");
            return null;
        }

        if (maps.containsKey(id)) return null;

        File configFile = new File(mapsDirectory, id + ".yml");
        Map map = new Map(plugin, configFile);
        maps.put(map.getId(), map);
        return map;
    }

    public @Nullable Map get(String id) {
        return maps.get(id);
    }

    public Collection<Map> getAllReady() {
        return maps.values().stream().filter(Map::isReady).toList();
    }

    public Collection<Map> getAll() {
        return Collections.unmodifiableCollection(maps.values());
    }
}
