package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static de.tmxx.survivalgames.command.util.CommandSnippets.getUser;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     Sets the main spawn position.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class AddDeathmatchSpawn implements Command {
    private final UserRegistry registry;
    private final JavaPlugin plugin;

    @Inject
    AddDeathmatchSpawn(UserRegistry registry, JavaPlugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "adddeathmatchspawn";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        List<SpawnPosition> spawns = SpawnPosition.fromList(plugin.getConfig().getList("deathmatch-spawns"));
        spawns.add(new SpawnPosition(user.getPlayer().getLocation()));
        plugin.getConfig().set("deathmatch-spawns", spawns);
        plugin.saveConfig();
        user.sendMessage("command.adddeathmatchspawn.success");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.setup";
    }

    @Override
    public List<String> aliases() {
        return List.of("adddmspawn", "adddm");
    }
}
