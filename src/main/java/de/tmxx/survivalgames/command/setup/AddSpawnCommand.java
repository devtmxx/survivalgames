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

import java.util.List;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     Adds a spawn to a specified map.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class AddSpawnCommand implements Command {
    private final JavaPlugin plugin;
    private final UserRegistry registry;
    private final MapManager mapManager;

    @Inject
    AddSpawnCommand(JavaPlugin plugin, UserRegistry registry, MapManager mapManager) {
        this.plugin = plugin;
        this.registry = registry;
        this.mapManager = mapManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "addspawn";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.addspawn.help");
            return;
        }

        String id = args[0];
        if (id.equalsIgnoreCase("deathmatch")) {
            addDeathmatchSpawn(user);
            return;
        }

        Map map =  getMap(mapManager, id, user);
        if (map == null) return;

        if (map.addSpawnPosition(user.getPlayer().getLocation())) {
            user.sendMessage("command.addspawn.success", id, map.amountOfSpawns());
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

    private void addDeathmatchSpawn(User user) {
        List<SpawnPosition> spawns = SpawnPosition.fromList(plugin.getConfig().getList("deathmatch-spawns"));
        spawns.add(new SpawnPosition(user.getPlayer().getLocation()));
        plugin.getConfig().set("deathmatch-spawns", spawns);
        plugin.saveConfig();
        user.sendMessage("command.addspawn.deathmatch");
    }
}
