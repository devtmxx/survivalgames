package de.tmxx.survivalgames.game;

import de.tmxx.survivalgames.user.User;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */

public interface Game {
    void startGame();
    void stopGame();

    void startTimer();
    void stopTimer();
    void resetTimer();
    void setTimer(int seconds);

    boolean isCounting();
    int secondsLeft();
    int secondsElapsed();

    void checkEnd();
    void forceStart(User user);

    User winner();
}
