package de.tmxx.survivalgames.auto;

import io.papermc.paper.command.brigadier.BasicCommand;

import java.util.List;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface AutoCommand extends BasicCommand {
    String name();

    default List<String> aliases() {
        return List.of();
    };
}
