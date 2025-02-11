package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.CommandSnippets.*;

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
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class CreateMapCommand implements AutoCommand {
    private final SurvivalGames plugin;

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
        User user = getUser(source);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.createmap.help");
            return;
        }

        String id = args[0];
        if (plugin.getMapManager().create(id) == null) {
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
