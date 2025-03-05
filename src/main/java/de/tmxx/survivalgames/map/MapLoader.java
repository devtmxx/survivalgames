package de.tmxx.survivalgames.map;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.MapsDirectory;
import de.tmxx.survivalgames.module.game.PluginLogger;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class MapLoader {
    private final Logger logger;
    private final File mapsDirectory;

    @Inject
    MapLoader(@PluginLogger Logger logger, @MapsDirectory File mapsDirectory) {
        this.logger = logger;
        this.mapsDirectory = mapsDirectory;
    }

    public World load(String name, boolean overwrite) {
        World world = Bukkit.getWorld(name);
        if (world != null) return world;

        copyWorld(name, overwrite);
        return WorldCreator.name(name).createWorld();
    }

    private void copyWorld(String name, boolean overwrite) {
        File worldDirectory = new File(Bukkit.getWorldContainer(), name);
        if (worldDirectory.exists() && !overwrite) return;

        if (worldDirectory.exists() && !deleteExistingWorld(name)) {
            if (!overwrite) return;

            logger.warning("Could neither delete nor move the old world. Now using a map where has already been played on.");
            return;
        }

        File source = new File(mapsDirectory, name);
        if (!source.exists()) {
            logger.warning("Cannot find source for world " + name);
            return;
        }

        try {
            FileUtils.copyDirectory(source, worldDirectory);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while copying directory", e);
        }
    }

    private boolean deleteExistingWorld(String name) {
        File worldDirectory = new File(Bukkit.getWorldContainer(), name);

        try {
            FileUtils.deleteDirectory(worldDirectory);
            return true;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while deleting world directory", e);

            logger.info("Trying to rename world instead");
            return worldDirectory.renameTo(new File(Bukkit.getWorldContainer(), UUID.randomUUID().toString()));
        }
    }
}
