package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.CommandSnippets.*;

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
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class SetSpawnCommand implements AutoCommand {
    private final SurvivalGames plugin;

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
        User user = getUser(source);
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
