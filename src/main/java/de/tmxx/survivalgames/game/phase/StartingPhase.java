package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.module.game.phase.Starting;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserPreparer;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
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
public class StartingPhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 20;

    private final Game game;
    private final UserRegistry registry;
    private final UserBroadcaster broadcaster;
    private final UserPreparer preparer;
    private final MapManager mapManager;
    private final FileConfiguration config;
    private final GamePhase nextPhase;
    private final ListenerRegistrar listenerRegistrar;

    @Inject
    StartingPhase(
            Game game,
            UserRegistry registry,
            UserBroadcaster broadcaster,
            UserPreparer preparer,
            @MainConfig FileConfiguration config,
            MapManager mapManager,
            @InGame GamePhase nextPhase,
            ListenerRegistrar listenerRegistrar
    ) {
        this.game = game;
        this.registry = registry;
        this.broadcaster = broadcaster;
        this.preparer = preparer;
        this.config = config;
        this.mapManager = mapManager;
        this.nextPhase = nextPhase;
        this.listenerRegistrar = listenerRegistrar;
    }

    @Override
    public void start() {
        listenerRegistrar.registerPhaseSpecific(Starting.class);

        Map map = mapManager.getVotedMap();
        registry.getUsers(UserState.PLAYING).forEach(user -> {
            Location spawnLocation = map.getNextSpawn();
            if (spawnLocation == null) {
                // kick the player if there is no spawn found (should not happen)
                user.getPlayer().kick(user.translate("kick.no-spawn"));
                return;
            }

            preparer.prepareUserForGame(user);
            user.getPlayer().teleport(spawnLocation);
        });

        game.startTimer();
    }

    @Override
    public void tick() {
        if (!game.isCounting()) return;

        int timeLeft = game.secondsLeft();
        if (!shouldBroadcastTimeLeft(timeLeft)) return;

        broadcaster.broadcast("timers.starting.chat." + (timeLeft == 1 ? "single" : "multiple"), timeLeft);
        broadcaster.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
    }

    @Override
    public void end() {
        listenerRegistrar.unregisterPhaseSpecific(Starting.class);
    }

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.starting", DEFAULT_COUNTDOWN_SECONDS);
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
