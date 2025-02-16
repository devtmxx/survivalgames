package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
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
 *     Creates a new map.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class CreateMapCommand implements Command {
    private final UserRegistry registry;
    private final MapManager mapManager;

    @Inject
    public CreateMapCommand(UserRegistry registry, MapManager mapManager) {
        this.registry = registry;
        this.mapManager = mapManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "createmap";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.createmap.help");
            return;
        }

        String id = args[0];
        if (mapManager.create(id) == null) {
            user.sendMessage("command.createmap.error", id);
            return;
        }

        user.sendMessage("command.createmap.success", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.createmap";
    }
}
