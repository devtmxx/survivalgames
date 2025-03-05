package de.tmxx.survivalgames.scoreboard;

import de.tmxx.survivalgames.user.User;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ScoreboardFactory {
    GameScoreboard createScoreboard(User user);
}
