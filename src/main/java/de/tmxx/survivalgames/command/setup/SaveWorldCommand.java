package de.tmxx.survivalgames.command.setup;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.command.Setup;
import de.tmxx.survivalgames.map.MapSaver;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.getUser;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Setup
public class SaveWorldCommand implements Command {
    private final UserRegistry registry;
    private final MapSaver saver;

    @Inject
    SaveWorldCommand(UserRegistry registry, MapSaver saver) {
        this.registry = registry;
        this.saver = saver;
    }

    @Override
    public String name() {
        return "saveworld";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.saveworld.help");
            return;
        }

        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            user.sendMessage("command.saveworld.not-found", worldName);
            return;
        }

        saver.save(world);
        user.sendMessage("command.saveworld.success", worldName);
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.setup";
    }
}
