package de.tmxx.survivalgames.game;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.game.phase.GamePhase;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RequiredArgsConstructor
public class Game implements Runnable {
    private final SurvivalGames plugin;

    private int taskId = -1;
    @Getter private GamePhase currentPhase = null;

    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 20L);
    }

    public void stop() {
        if (taskId == -1) return;

        Bukkit.getScheduler().cancelTask(taskId);
        taskId = -1;
    }

    public void changeGamePhase(GamePhase nextPhase) {
        if (currentPhase != null) currentPhase.end();
        currentPhase = nextPhase;
        nextPhase.start();
    }

    @Override
    public void run() {
        if (currentPhase == null) return;
        currentPhase.tick();
    }
}
