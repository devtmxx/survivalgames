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

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

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
public class SetSpawnCommand implements Command {
    private final UserRegistry registry;
    private final JavaPlugin plugin;

    @Inject
    SetSpawnCommand(UserRegistry registry, JavaPlugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "setspawn";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        plugin.getConfig().set("spawn", new SpawnPosition(user.getPlayer().getLocation()));
        plugin.saveConfig();
        user.sendMessage("command.setspawn.success");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.setspawn";
    }
}
