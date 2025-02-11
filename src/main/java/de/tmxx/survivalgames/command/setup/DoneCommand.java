package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class DoneCommand implements AutoCommand {
    private final SurvivalGames plugin;

    @Override
    public String name() {
        return "done";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source);
        if (user == null) return;

        plugin.getConfig().set("setup", false);
        plugin.saveConfig();

        user.sendMessage("command.done.restart");
        Bukkit.getScheduler().runTaskLater(plugin, Bukkit::shutdown, 20L * 10);
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.command.done";
    }
}
