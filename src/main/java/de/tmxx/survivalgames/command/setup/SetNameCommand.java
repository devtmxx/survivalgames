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
 *     Sets the display name of a map.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class SetNameCommand implements Command {
    private final UserRegistry registry;
    private final MapManager mapManager;

    @Inject
    SetNameCommand(UserRegistry registry, MapManager mapManager) {
        this.registry = registry;
        this.mapManager = mapManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "setname";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length < 2) {
            user.sendMessage("command.setname.help");
            return;
        }

        String id = args[0];
        Map map = getMap(mapManager, id, user);
        if (map == null) return;

        String name = sumArgs(1, args);
        map.setName(name);
        user.sendMessage("command.setname.success", id, name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.setup";
    }
}
