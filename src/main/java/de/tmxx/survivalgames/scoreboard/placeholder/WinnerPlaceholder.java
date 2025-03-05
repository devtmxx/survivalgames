package de.tmxx.survivalgames.scoreboard.placeholder;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.user.User;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class WinnerPlaceholder implements Placeholder {
    private final Game game;

    @Inject
    WinnerPlaceholder(Game game) {
        this.game = game;
    }

    @Override
    public String variable() {
        return "%winner%";
    }

    @Override
    public String replacement(Locale locale) {
        User winner = game.winner();
        return winner == null ? "" : winner.getName();
    }
}
