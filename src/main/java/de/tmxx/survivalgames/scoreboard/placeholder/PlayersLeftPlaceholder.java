package de.tmxx.survivalgames.scoreboard.placeholder;

import com.google.inject.Inject;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class PlayersLeftPlaceholder implements Placeholder {
    private final UserRegistry registry;

    @Inject
    PlayersLeftPlaceholder(UserRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String variable() {
        return "%players-left%";
    }

    @Override
    public String replacement(Locale locale) {
        return String.valueOf(registry.getUsers(UserState.PLAYING).size());
    }
}
