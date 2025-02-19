package de.tmxx.survivalgames.command.game;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 19.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@de.tmxx.survivalgames.command.Game
public class StartCommand implements Command {
    private final UserRegistry registry;
    private final Game game;
    private final GamePhase lobbyPhase;

    @Inject
    StartCommand(UserRegistry registry, Game game, @Lobby GamePhase lobbyPhase) {
        this.registry = registry;
        this.game = game;
        this.lobbyPhase = lobbyPhase;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return "start";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (!game.currentPhase().equals(lobbyPhase)) {
            user.sendMessage("command.start.only-lobby");
            return;
        }

        game.forceStart(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @Nullable String permission() {
        return "survivalgames.command.start";
    }
}
