package de.tmxx.survivalgames.map;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.MapsDirectory;
import de.tmxx.survivalgames.module.game.PluginLogger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class MapSaver {
    private final Logger logger;
    private final File mapsDirectory;

    @Inject
    MapSaver(@PluginLogger Logger logger, @MapsDirectory File mapsDirectory) {
        this.logger = logger;
        this.mapsDirectory = mapsDirectory;
    }

    public boolean save(World world) {
        prepareWorld(world);
        return copyAndReplaceWorld(world);
    }

    private void prepareWorld(World world) {
        World defaultWorld = Bukkit.getWorlds().getFirst();
        if (defaultWorld.equals(world)) return;

        world.getPlayers().forEach(player -> player.teleport(defaultWorld.getSpawnLocation()));
        Bukkit.unloadWorld(world, true);
    }

    private boolean copyAndReplaceWorld(World world) {
        File worldDirectory = new File(Bukkit.getWorldContainer(), world.getName());
        if (!worldDirectory.exists()) {
            logger.warning("Could not find world directory to save");
            return false;
        }

        File destination = new File(mapsDirectory, world.getName());

        try {
            if (destination.exists()) FileUtils.deleteDirectory(destination);

            FileUtils.copyDirectory(worldDirectory, destination);
            FileUtils.deleteDirectory(worldDirectory);
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not save world to map", e);
            return false;
        }
    }
}
