package de.tmxx.survivalgames.game.phase;

import org.bukkit.Location;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface GamePhase {
    int TEN_MINUTES = 600;
    int FIVE_MINUTES = 300;
    int ONE_MINUTE = 60;

    void start();
    void tick();
    void end();
    int countdownSeconds();
    Location spawnLocation();
    void nextPhase();

    default boolean shouldBroadcastTimeLeft(int timeLeft) {
        if (timeLeft > TEN_MINUTES) return timeLeft % TEN_MINUTES == 0;
        if (timeLeft >= FIVE_MINUTES) return timeLeft % FIVE_MINUTES == 0;
        if (timeLeft > ONE_MINUTE) return timeLeft % ONE_MINUTE == 0;
        return timeLeft % 10 == 0 || timeLeft <= 5;
    }
}
