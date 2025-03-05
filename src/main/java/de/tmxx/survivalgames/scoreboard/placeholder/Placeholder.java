package de.tmxx.survivalgames.scoreboard.placeholder;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface Placeholder {
    String variable();
    String replacement(Locale locale);
}
