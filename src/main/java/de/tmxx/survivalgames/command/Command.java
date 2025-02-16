package de.tmxx.survivalgames.command;

import io.papermc.paper.command.brigadier.BasicCommand;

import java.util.List;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     An interface used to signal to the {@link CommandRegistrar} that a command should automatically be registered. This
 *     provides some basic information necessary to register a command using the new brigadier command system.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface Command extends BasicCommand {
    /**
     * Retrieves the name of the command to register. This name should always be in lower case for easier access by the
     * player since commands are registered case-sensitive.
     *
     * @return the name of the command
     */
    String name();

    /**
     * Retrieves the aliases of the command to register. Analogous to the name are command aliases case-sensitive and
     * should be held lower case for easier access by the player.
     *
     * @return a list of command aliases
     */
    default List<String> aliases() {
        return List.of();
    };
}
