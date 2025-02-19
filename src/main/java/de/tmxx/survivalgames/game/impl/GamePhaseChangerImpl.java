package de.tmxx.survivalgames.game.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.module.game.phase.Ending;
import org.jetbrains.annotations.Nullable;

/**
 * Project: survivalgames
 * 19.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class GamePhaseChangerImpl implements GamePhaseChanger {
    private final GamePhase endingPhase;
    private GamePhase currentPhase;

    @Inject
    GamePhaseChangerImpl(@Ending GamePhase endingPhase) {
        this.endingPhase = endingPhase;
    }

    @Override
    public @Nullable GamePhase currentPhase() {
        return currentPhase;
    }

    @Override
    public void changeGamePhase(GamePhase newPhase) {
        if (currentPhase != null) currentPhase.end();
        currentPhase = newPhase;
        newPhase.start();
    }

    @Override
    public void endGame() {
        changeGamePhase(endingPhase);
    }
}
