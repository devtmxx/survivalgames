package de.tmxx.survivalgames.game.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class GameImpl implements Game, Runnable {
    private final JavaPlugin plugin;

    private final AtomicBoolean counting = new AtomicBoolean(false);
    private int taskId = -1;
    private GamePhase currentPhase = null;
    private int countdownSeconds = -1;
    private int currentTick = 0;

    @Inject
    GameImpl(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void startGame() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    @Override
    public void stopGame() {
        if (taskId == -1) return;

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    @Override
    public void startTimer() {
        counting.set(true);
    }

    @Override
    public void stopTimer() {
        counting.set(false);
    }

    @Override
    public void resetTimer() {
        currentTick = 0;
    }

    @Override
    public void setTimer(int seconds) {
        currentTick = seconds;
    }

    @Override
    public boolean isCounting() {
        return counting.get();
    }

    @Override
    public int secondsLeft() {
        return countdownSeconds - currentTick;
    }

    @Override
    public int secondsElapsed() {
        return currentTick;
    }

    @Override
    public GamePhase currentPhase() {
        return currentPhase;
    }

    public void changeGamePhase(GamePhase nextPhase) {
        if (currentPhase != null) endPhase(currentPhase);
        currentPhase = nextPhase;
        startPhase(currentPhase);
    }

    @Override
    public void run() {
        if (currentPhase == null) return;
        if (counting.get()) {
            currentTick++;
            if (currentTick >= countdownSeconds) {
                currentPhase.nextPhase();
                return;
            }
        }

        currentPhase.tick();
    }

    public void checkEnd() {

    }

    @Override
    public void forceStart(User user) {
        if (secondsLeft() <= 10) {
            user.sendMessage("start.already-starting");
            return;
        }

        if (!counting.get()) {
            user.sendMessage("start.unable");
            return;
        }

        // starts the game instantly if the countdown time is <= 10 seconds, otherwise sets the time left to 10 seconds
        currentTick = Math.max(countdownSeconds - 10, 0);
        user.sendMessage("start.done");
    }

    private void startPhase(GamePhase phase) {
        countdownSeconds = phase.countdownSeconds();
        currentTick = 0;
        phase.start();
    }

    private void endPhase(GamePhase phase) {
        counting.set(false);
        phase.end();
    }
}
