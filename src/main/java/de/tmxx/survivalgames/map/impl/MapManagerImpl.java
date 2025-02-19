package de.tmxx.survivalgames.map.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapFactory;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.game.MapsDirectory;
import de.tmxx.survivalgames.module.game.PluginLogger;
import de.tmxx.survivalgames.user.UserBroadcaster;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class MapManagerImpl implements MapManager {
    private final File mapsDirectory;
    private final Logger logger;
    private final MapFactory factory;
    private final UserBroadcaster broadcaster;
    private final java.util.Map<String, Map> maps = new HashMap<>();

    private boolean hasVotingEnded = false;
    private Map votedMap = null;

    @Inject
    MapManagerImpl(
            @PluginLogger Logger logger,
            @MapsDirectory File mapsDirectory,
            MapFactory factory,
            UserBroadcaster broadcaster
    ) {
        this.logger = logger;
        this.factory = factory;
        this.broadcaster = broadcaster;

        if (!mapsDirectory.exists() && !mapsDirectory.mkdirs()) {
            logger.severe("Could not create maps directory");
            mapsDirectory = null;
        }

        this.mapsDirectory = mapsDirectory;
    }

    @Override
    public void load() {
        if (mapsDirectory == null) return;

        File[] files = mapsDirectory.listFiles();
        if (files == null) return;

        for (File file : files) {
            Map map = factory.create(file);
            maps.put(map.getId(), map);

            logger.info("Loaded map data for " + map.getId());
        }
    }

    @Override
    public @Nullable Map create(String id) {
        if (mapsDirectory == null) {
            logger.warning("Cannot create map data if the maps directory is not accessible");
            return null;
        }

        if (maps.containsKey(id)) return null;

        File configFile = new File(mapsDirectory, id + ".yml");
        Map map = factory.create(configFile);
        maps.put(map.getId(), map);
        return map;
    }

    @Override
    public @Nullable Map get(String id) {
        return maps.get(id);
    }

    @Override
    public Collection<Map> getUsableMaps() {
        return maps.values().stream().filter(Map::isUsable).toList();
    }

    @Override
    public Collection<Map> getAll() {
        return Collections.unmodifiableCollection(maps.values());
    }

    @Override
    public void endVoting() {
        hasVotingEnded = true;

        chooseMap();
        if (votedMap == null) {
            // we should not reach a state where there is no voted map
            Bukkit.shutdown();
            return;
        }

        votedMap.loadWorld();

        // broadcast the map to the players
        broadcaster.broadcast("vote.done", votedMap.getName(), votedMap.getAuthor());
    }

    @Override
    public boolean hasVotingEnded() {
        return hasVotingEnded;
    }

    @Override
    public Map getVotedMap() {
        return votedMap;
    }

    private void chooseMap() {
        // a list of map sorted by votes in descending order
        List<Map> sortedByVotes = getUsableMaps().stream().sorted(Comparator.comparingInt(Map::getVotes).reversed()).toList();
        if (sortedByVotes.isEmpty()) return;

        // no more calculations if there is only one map
        if (sortedByVotes.size() == 1) {
            votedMap = sortedByVotes.getFirst();
            return;
        }

        // filter the maps with the most votes and choose a random one
        int mostVotes = sortedByVotes.getFirst().getVotes();
        sortedByVotes = sortedByVotes.stream().filter(map -> map.getVotes() == mostVotes).toList();
        votedMap = sortedByVotes.get((int) Math.floor(Math.random() * sortedByVotes.size()));
    }
}
