package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.map.Map;
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
 *     Saves changes made to a map config.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class SaveCommand implements AutoCommand {
    private final SurvivalGames plugin;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "save";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.save.help");
            return;
        }

        String id = args[0];
        Map map = getMap(plugin.getMapManager(), id, user);
        if (map == null) return;

        map.save();
        user.sendMessage("command.save.success", id);
        user.sendMessage("command.save.ready." + map.isReady());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.save";
    }
}
