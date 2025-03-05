package de.tmxx.survivalgames.map.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapLoader;
import de.tmxx.survivalgames.module.config.MaxPlayers;
import de.tmxx.survivalgames.module.game.PluginLogger;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class MapImpl implements Map {
    private final Logger logger;
    private final File configFile;
    private final int maxPlayers;
    private final MapLoader loader;
    @Getter private final String id;

    // persistent values
    private FileConfiguration config = null;
    @Getter @Setter private String name = null;
    @Setter private String world = null;
    @Getter @Setter private String author = null;
    private int currentSpawnIndex = 0;
    private final List<SpawnPosition> spawnPositions = new ArrayList<>();
    private SpawnPosition spectatorSpawn = null;

    // temporary values
    private final Set<UUID> votes = new HashSet<>();

    @Inject
    MapImpl(
            @PluginLogger Logger logger,
            @MaxPlayers int maxPlayers,
            @Assisted File configFile,
            MapLoader loader) {
        this.logger = logger;
        this.configFile = configFile;
        this.maxPlayers = maxPlayers;
        this.loader = loader;

        id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));

        load();
    }

    private void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
        name = config.getString("name");
        world = config.getString("world");
        author = config.getString("author");
        spectatorSpawn = config.getSerializable("spectator", SpawnPosition.class);

        spawnPositions.clear();
        spawnPositions.addAll(SpawnPosition.fromList(config.getList("spawns")));
    }

    public void loadWorld() {
        if (world == null) {
            logger.warning("Cannot load an unknown world");
            return;
        }

        loader.load(world, true);
    }

    public boolean isUsable() {
        return world != null &&
                maxPlayers <= spawnPositions.size() &&
                spectatorSpawn != null;
    }

    public @Nullable Location getNextSpawn() {
        if (currentSpawnIndex >= spawnPositions.size()) return null;

        return spawnPositions.get(currentSpawnIndex++).getCentered();
    }

    public boolean addSpawnPosition(Location location) {
        if (world == null) world = location.getWorld().getName();
        if (!world.equals(location.getWorld().getName())) return false;

        spawnPositions.add(new SpawnPosition(location));
        return true;
    }

    public int amountOfSpawns() {
        return spawnPositions.size();
    }

    public boolean setSpectatorSpawn(Location location) {
        if (world == null) world = location.getWorld().getName();
        if (!world.equals(location.getWorld().getName())) return false;

        spectatorSpawn = new SpawnPosition(location);
        return true;
    }

    @Override
    public Location getSpectatorSpawn() {
        return spectatorSpawn.getCentered();
    }

    @Override
    public void save() {
        if (config == null) config = new YamlConfiguration();

        config.set("name", name);
        config.set("world", world);
        config.set("author", author);
        config.set("spectator", spectatorSpawn);
        config.set("spawns", spawnPositions);
        try {
            config.save(configFile);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while saving map config", e);
        }
    }

    public void castVote(UUID uniqueId) {
        votes.add(uniqueId);
    }

    @Override
    public boolean hasVoted(UUID uniqueId) {
        return votes.contains(uniqueId);
    }

    public int getVotes() {
        return votes.size();
    }
}
