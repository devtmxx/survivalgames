package de.tmxx.survivalgames.game.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.stats.StatsKey;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
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
    private final GamePhaseChanger gamePhaseChanger;
    private final UserRegistry registry;

    private final AtomicBoolean counting = new AtomicBoolean(true);
    private int taskId = -1;
    private int countdownSeconds = -1;
    private int currentTick = 0;
    private User winner = null;

    @Inject
    GameImpl(JavaPlugin plugin, GamePhaseChanger gamePhaseChanger, UserRegistry registry) {
        this.plugin = plugin;
        this.gamePhaseChanger = gamePhaseChanger;
        this.registry = registry;
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
        GamePhase currentPhase = gamePhaseChanger.currentPhase();
        if (currentPhase == null) return;

        currentTick = 0;
        countdownSeconds = currentPhase.countdownSeconds();
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
    public void run() {
        GamePhase currentPhase = gamePhaseChanger.currentPhase();
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
        List<User> playing = registry.getUsers(UserState.PLAYING);

        // the only scenario where a player wins is if he is the only one left
        if (playing.size() != 1) return;

        winner = playing.getFirst();
        winner.retrieveStats(stats -> stats.add(StatsKey.WINS, 1));
        gamePhaseChanger.endGame();
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

    @Override
    public User winner() {
        return winner;
    }
}
