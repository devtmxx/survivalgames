package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.module.game.phase.Starting;
import de.tmxx.survivalgames.user.UserBroadcaster;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class LobbyPhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 60;

    private final Game game;
    private final UserBroadcaster broadcaster;
    private final FileConfiguration config;
    private final GamePhase nextPhase;
    private final ListenerRegistrar listenerRegistrar;

    @Inject
    public LobbyPhase(
            Game game,
            UserBroadcaster broadcaster,
            @MainConfig FileConfiguration config,
            @Starting GamePhase nextPhase,
            ListenerRegistrar listenerRegistrar
    ) {
        this.game = game;
        this.broadcaster = broadcaster;
        this.config = config;
        this.nextPhase = nextPhase;
        this.listenerRegistrar = listenerRegistrar;
    }

    @Override
    public void start() {
        listenerRegistrar.registerPhaseSpecific(Lobby.class);

        for (World world : Bukkit.getWorlds()) {
            world.setTime(7000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setStorm(false);
            world.setThundering(false);
        }
    }

    @Override
    public void tick() {
        if (game.isCounting()) {
            int timeLeft = game.secondsLeft();
            String key = "timers.lobby.action-bar." + (timeLeft == 1 ? "single" : "multiple");
            broadcaster.broadcastActionBar(key, timeLeft);

            if (shouldBroadcastTimeLeft(timeLeft)) {
                key = "timers.lobby.chat." + (timeLeft == 1 ? "single" : "multiple");
                broadcaster.broadcast(key, timeLeft);
                broadcaster.broadcastSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
            }

            if (timeLeft <= 5) {
                broadcaster.broadcastTitle("timers.lobby.title." + timeLeft, null, 200, 600, 200);
            }
        } else {
            broadcaster.broadcastActionBar("timers.lobby.action-bar.waiting");
        }
    }

    @Override
    public void end() {
        broadcaster.broadcastTitle("timers.lobby.title.0", null, 200, 600, 200);
        broadcaster.broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        listenerRegistrar.unregisterPhaseSpecific(Lobby.class);
    }

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.lobby", DEFAULT_COUNTDOWN_SECONDS);
    }

    @Override
    public void nextPhase() {
        game.changeGamePhase(nextPhase);
    }
}
