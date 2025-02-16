package de.tmxx.survivalgames.command.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.CommandRegistrar;
import de.tmxx.survivalgames.command.Game;
import de.tmxx.survivalgames.module.game.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class GameCommandRegistrar implements CommandRegistrar {
    private final JavaPlugin plugin;
    private final Logger logger;

    private boolean done = false;
    private final List<Class<? extends Command>> commands = new ArrayList<>();

    @Inject
    public GameCommandRegistrar(JavaPlugin plugin, @PluginLogger Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void register() {
        discoverSetupCommandsIfNotAlreadyDone();
        register(commands, plugin);
    }

    private void discoverSetupCommandsIfNotAlreadyDone() {
        if (done) return;
        done = true;

        commands.addAll(loadClasses(Game.class, logger));
    }
}
