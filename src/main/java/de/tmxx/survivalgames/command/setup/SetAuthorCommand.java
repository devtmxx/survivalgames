package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     Sets the author name of a map.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class SetAuthorCommand implements Command {
    private final UserRegistry registry;
    private final MapManager mapManager;

    @Inject
    public SetAuthorCommand(UserRegistry registry, MapManager mapManager) {
        this.registry = registry;
        this.mapManager = mapManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "setauthor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length < 2) {
            user.sendMessage("command.setauthor.help");
            return;
        }

        String id = args[0];
        Map map = getMap(mapManager, id, user);
        if (map == null) return;

        String author = sumArgs(1, args);
        map.setAuthor(author);
        user.sendMessage("command.setauthor.success", id, author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.setauthor";
    }
}
