package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     Disables setup mode and restarts the server.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class DoneCommand implements Command {
    private final UserRegistry registry;
    private final JavaPlugin plugin;

    @Inject
    public DoneCommand(UserRegistry registry, JavaPlugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "done";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        plugin.getConfig().set("setup", false);
        plugin.saveConfig();

        user.sendMessage("command.done.restart");
        Bukkit.getScheduler().runTaskLater(plugin, Bukkit::shutdown, 20L * 10);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.done";
    }
}
