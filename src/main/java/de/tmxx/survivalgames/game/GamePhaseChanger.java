package de.tmxx.survivalgames.game;

import de.tmxx.survivalgames.game.phase.GamePhase;
import org.jetbrains.annotations.Nullable;

/**
 * Project: survivalgames
 * 19.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface GamePhaseChanger {
    @Nullable GamePhase currentPhase();
    void changeGamePhase(GamePhase newPhase);
    void endGame();
}
