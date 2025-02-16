package de.tmxx.survivalgames.game;

import de.tmxx.survivalgames.game.phase.GamePhase;

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
    boolean isCounting();
    int secondsLeft();
    int secondsElapsed();
    GamePhase currentPhase();
    void changeGamePhase(GamePhase nextPhase);
    void checkEnd();
}
