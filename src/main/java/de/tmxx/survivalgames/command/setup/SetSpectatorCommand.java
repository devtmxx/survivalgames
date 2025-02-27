package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     Sets the spectator spawn position.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class SetSpectatorCommand implements Command {
    private final JavaPlugin plugin;
    private final UserRegistry registry;
    private final MapManager mapManager;

    @Inject
    SetSpectatorCommand(JavaPlugin plugin, UserRegistry registry, MapManager mapManager) {
        this.plugin = plugin;
        this.registry = registry;
        this.mapManager = mapManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "setspectator";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.setspectator.help");
            return;
        }

        String id = args[0];
        if (id.equalsIgnoreCase("deathmatch")) {
            setDeathMatchSpectatorSpawn(user);
            return;
        }

        Map map = getMap(mapManager, id, user);
        if (map == null) return;

        if (map.setSpectatorSpawn(user.getPlayer().getLocation())) {
            user.sendMessage("command.setspectator.success", id);
        } else {
            user.sendMessage("command.common.wrong-world");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.setup";
    }

    private void setDeathMatchSpectatorSpawn(User user) {
        plugin.getConfig().set("deathmatch-spectator", new SpawnPosition(user.getPlayer().getLocation()));
        plugin.saveConfig();

        user.sendMessage("command.setspectator.deathmatch");
    }
}
