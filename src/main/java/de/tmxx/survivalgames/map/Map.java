package de.tmxx.survivalgames.map;

import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.SurvivalGames;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class Map {
    private final SurvivalGames plugin;
    private final File configFile;
    @Getter private final String id;

    // persistent values
    private FileConfiguration config = null;
    @Getter @Setter private String name = null;
    @Setter private String world = null;
    @Getter @Setter private String author = null;
    private final List<SpawnPosition> spawnPositions = new ArrayList<>();
    private SpawnPosition spectatorSpawn = null;

    // temporary values
    private final Set<UUID> votes = new HashSet<>();

    public Map(SurvivalGames plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
        id = configFile.getName().substring(0, configFile.getName().lastIndexOf("."));
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(configFile);
        name = config.getString("name");
        world = config.getString("world");
        author = config.getString("author");
        spectatorSpawn = config.getSerializable("spectator", SpawnPosition.class);

        spawnPositions.clear();
        List<?> spawns = config.getList("spawns");
        if (spawns != null) {
            for (Object o : spawns) {
                if (!(o instanceof SpawnPosition position)) continue;

                spawnPositions.add(position);
            }
        }
    }

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
            plugin.getLogger().log(Level.WARNING, "Error while saving map config", e);
        }
    }

    public void loadWorld() {
        if (world == null) {
            plugin.getLogger().warning("Cannot load an unknown world");
            return;
        }

        if (Bukkit.getWorld(world) != null) return;

        copyWorld();
        Bukkit.createWorld(WorldCreator.name(world));
    }

    public boolean isReady() {
        return world != null &&
                plugin.getMaxPlayers() <= spawnPositions.size() &&
                spectatorSpawn != null;
    }

    public @Nullable Location getSpawnPosition(int index) {
        if (index >= spawnPositions.size()) return null;

        return spawnPositions.get(index).getCentered();
    }

    public boolean addSpawnPosition(Location location) {
        if (world == null) world = location.getWorld().getName();
        if (!world.equals(location.getWorld().getName())) return false;

        spawnPositions.add(new SpawnPosition(location));
        return true;
    }

    public int amountOfSpawnPositions() {
        return spawnPositions.size();
    }

    public boolean setSpectatorSpawn(Location location) {
        if (world == null) world = location.getWorld().getName();
        if (!world.equals(location.getWorld().getName())) return false;

        spectatorSpawn = new SpawnPosition(location);
        return true;
    }

    public boolean castVote(UUID uniqueId) {
        if (votes.contains(uniqueId)) return false;

        votes.add(uniqueId);
        return true;
    }

    public int getVotes() {
        return votes.size();
    }

    private void copyWorld() {
        File worldDirectory = new File(Bukkit.getWorldContainer(), world);
        if (worldDirectory.exists()) return;

        File source = new File(plugin.getConfig().getString("worlds-container", "worlds"), world);
        if (!source.exists()) {
            plugin.getLogger().warning("Cannot find source for world " + world);
            return;
        }

        try {
            FileUtils.copyDirectory(source, worldDirectory);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while copying directory", e);
        }
    }
}
