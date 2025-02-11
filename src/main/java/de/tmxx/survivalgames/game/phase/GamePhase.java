package de.tmxx.survivalgames.game.phase;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegistrar;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GameState;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public abstract class GamePhase {
    protected final SurvivalGames plugin;
    protected final Game game;
    protected final GameState gameState;
    private final AtomicBoolean counting = new AtomicBoolean(false);
    private final int startTimer;

    private int currentTick = 0;

    public GamePhase(SurvivalGames plugin, GameState gameState) {
        this.plugin = plugin;
        game = plugin.getGame();
        this.gameState = gameState;

        startTimer = plugin.getConfig().getInt("timers." + gameState.name().toLowerCase().replaceAll("_", "-"), 0);
    }

    public void start() {
        AutoRegistrar.registerPhaseListeners(plugin, gameState);
        onStart();
    }

    public void tick() {
        if (counting.get()) {
            currentTick++;
            if (currentTick >= startTimer) {
                end();
                return;
            }
        }

        onTick();
    }

    public void end() {
        counting.set(false);
        onEnd();
        AutoRegistrar.unregisterPhaseListeners(gameState);
    }

    public void reset() {
        currentTick = 0;
    }

    public void setCounting(boolean counting) {
        this.counting.set(counting);
    }

    public boolean isCounting() {
        return counting.get();
    }

    public int getTimeElapsed() {
        return currentTick;
    }

    public int getTimeLeft() {
        return startTimer - currentTick;
    }

    public abstract void onStart();
    public abstract void onTick();
    public abstract void onEnd();
}
