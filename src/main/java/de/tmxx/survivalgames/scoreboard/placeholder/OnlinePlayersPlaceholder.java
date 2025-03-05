package de.tmxx.survivalgames.scoreboard.placeholder;

import org.bukkit.Bukkit;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class OnlinePlayersPlaceholder implements Placeholder {
    @Override
    public String variable() {
        return "%online%";
    }

    @Override
    public String replacement(Locale locale) {
        return String.valueOf(Bukkit.getOnlinePlayers().size());
    }
}
