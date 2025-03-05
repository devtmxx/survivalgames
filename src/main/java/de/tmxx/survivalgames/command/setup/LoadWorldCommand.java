package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.map.MapLoader;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class LoadWorldCommand implements Command {
    private final UserRegistry registry;
    private final MapLoader loader;

    @Inject
    LoadWorldCommand(UserRegistry registry, MapLoader loader) {
        this.registry = registry;
        this.loader = loader;
    }

    @Override
    public String name() {
        return "loadworld";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.loadworld.help");
            return;
        }

        String worldName = args[0];
        World world = loader.load(worldName);
        user.sendMessage("command.loadworld.success", worldName);
        user.getPlayer().teleport(world.getSpawnLocation());
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.setup";
    }
}
