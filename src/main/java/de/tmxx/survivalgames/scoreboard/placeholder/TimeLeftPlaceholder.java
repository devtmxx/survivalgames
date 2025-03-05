package de.tmxx.survivalgames.scoreboard.placeholder;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class TimeLeftPlaceholder implements Placeholder {
    private final Game game;

    @Inject
    TimeLeftPlaceholder(Game game) {
        this.game = game;
    }

    @Override
    public String variable() {
        return "%time-left%";
    }

    @Override
    public String replacement(Locale locale) {
        int secondsLeft = game.secondsLeft();
        int seconds = secondsLeft % 60;
        int minutes = secondsLeft / 60 % 60;
        return beautify(minutes) + ":" + beautify(seconds);
    }

    private String beautify(int time) {
        if (time < 10) return "0" + time;
        return String.valueOf(time);
    }
}
