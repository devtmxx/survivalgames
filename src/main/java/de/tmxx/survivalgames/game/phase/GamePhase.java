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
    void start();
    void tick();
    void end();
    int countdownSeconds();
    Location spawnLocation();
    void nextPhase();

    default boolean shouldBroadcastTimeLeft(int timeLeft) {
        return timeLeft % 10 == 0 || timeLeft <= 5;
    }
}
