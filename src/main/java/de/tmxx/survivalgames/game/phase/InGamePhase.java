package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.UserBroadcaster;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Project: survivalgames
 * 16.02.25
 * 
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class InGamePhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 1200; // 20 minutes

    private final Game game;
    private final MapManager mapManager;
    private final ListenerRegistrar listenerRegistrar;
    private final UserBroadcaster broadcaster;
    private final FileConfiguration config;
    private final GamePhase nextPhase;

    @Inject
    InGamePhase(
            Game game,
            MapManager mapManager,
            ListenerRegistrar listenerRegistrar,
            UserBroadcaster broadcaster,
            @MainConfig FileConfiguration config,
            @DeathMatch GamePhase nextPhase
    ) {
        this.game = game;
        this.mapManager = mapManager;
        this.listenerRegistrar = listenerRegistrar;
        this.broadcaster = broadcaster;
        this.config = config;
        this.nextPhase = nextPhase;
    }

    @Override
    public void start() {
        listenerRegistrar.registerPhaseSpecific(InGame.class);

        broadcaster.broadcast("timers.in-game.chat.start");
        broadcaster.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);

        game.startTimer();
    }

    @Override
    public void tick() {
        if (!game.isCounting()) return;

        int timeLeft = game.secondsLeft();
        if (!shouldBroadcastTimeLeft(timeLeft)) return;

        boolean displayMinutes = timeLeft > 60;
        int displayTime = displayMinutes ? timeLeft / 60 : timeLeft;
        broadcaster.broadcast(
                "timers.in-game.chat." + (displayMinutes ? "minutes" : "seconds") + "." + (displayTime == 1 ? "single" : "multiple"),
                displayTime
        );
        broadcaster.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
    }

    @Override
    public void end() {
        listenerRegistrar.unregisterPhaseSpecific(InGame.class);
    }

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.in-game", DEFAULT_COUNTDOWN_SECONDS);
    }

    @Override
    public Location spawnLocation() {
        return mapManager.getVotedMap().getSpectatorSpawn();
    }

    @Override
    public void nextPhase() {
        game.changeGamePhase(nextPhase);
    }
}
