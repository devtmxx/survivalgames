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
 *     Sets the author name of a map.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class SetAuthorCommand implements AutoCommand {
    private final SurvivalGames plugin;

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "setauthor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source);
        if (user == null) return;

        if (args.length < 2) {
            user.sendMessage("command.setauthor.help");
            return;
        }

        String id = args[0];
        Map map = getMap(plugin.getMapManager(), id, user);
        if (map == null) return;

        String author = sumArgs(1, args);
        map.setAuthor(author);
        user.sendMessage("command.setauthor.success", id, author);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.setauthor";
    }
}
