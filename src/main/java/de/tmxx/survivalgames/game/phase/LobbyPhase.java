package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.config.MinPlayers;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.module.game.phase.Starting;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserPreparer;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class LobbyPhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 60;
    public static final int VOTING_END_SECONDS = 10;

    private final Game game;
    private final UserRegistry registry;
    private final UserBroadcaster broadcaster;
    private final MapManager mapManager;
    private final FileConfiguration config;
    private final GamePhase nextPhase;
    private final ListenerRegistrar listenerRegistrar;
    private final GamePhaseChanger gamePhaseChanger;
    private final int minPlayers;

    @Inject
    LobbyPhase(
            Game game,
            UserRegistry registry,
            UserBroadcaster broadcaster,
            MapManager mapManager,
            @MainConfig FileConfiguration config,
            @Starting GamePhase nextPhase,
            ListenerRegistrar listenerRegistrar,
            GamePhaseChanger gamePhaseChanger,
            @MinPlayers int minPlayers
    ) {
        this.game = game;
        this.registry = registry;
        this.broadcaster = broadcaster;
        this.mapManager = mapManager;
        this.config = config;
        this.nextPhase = nextPhase;
        this.listenerRegistrar = listenerRegistrar;
        this.gamePhaseChanger = gamePhaseChanger;
        this.minPlayers = minPlayers;
    }

    @Override
    public void start() {
        game.resetTimer();
        listenerRegistrar.registerPhaseSpecific(Lobby.class);

        if (registry.getUsers(UserState.PLAYING).size() < minPlayers) {
            game.stopTimer();
        }
    }

    @Override
    public void tick() {
        broadcaster.broadcastScoreboardUpdate();

        if (!game.isCounting()) {
            broadcaster.broadcastActionBar("timers.lobby.action-bar.waiting");
            return;
        }

        int timeLeft = game.secondsLeft();
        String key = "timers.lobby.action-bar." + (timeLeft == 1 ? "single" : "multiple");
        broadcaster.broadcastActionBar(key, timeLeft);

        // end the map voting 10 seconds before the game starts. this will give the world enough time to load but
        // may cause a small lag when the timer reaches 10 seconds left. we accept the small lag in order to save
        // server resources overall.
        if (timeLeft <= VOTING_END_SECONDS && !mapManager.hasVotingEnded()) {
            mapManager.endVoting();

            // remove the voting item from inventories
            registry.getOnlineUsers().forEach(user -> user.getPlayer().getInventory().setItem(UserPreparer.VOTE_ITEM_SLOT, null));
        }

        if (shouldBroadcastTimeLeft(timeLeft)) {
            key = "timers.lobby.chat." + (timeLeft == 1 ? "single" : "multiple");
            broadcaster.broadcast(key, timeLeft);
            broadcaster.broadcastSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        }

        if (timeLeft <= 5) {
            broadcaster.broadcastTitle("timers.lobby.title." + timeLeft, null, 200, 600, 200);
        }
    }

    @Override
    public void end() {
        broadcaster.broadcastTitle("timers.lobby.title.0", null, 200, 600, 200);
        broadcaster.broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 1F, 1F);
        broadcaster.broadcastScoreboardReset();
        listenerRegistrar.unregisterPhaseSpecific(Lobby.class);
    }

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.lobby", DEFAULT_COUNTDOWN_SECONDS);
    }

    @Override
    public Location spawnLocation() {
        ConfigurationSection section = config.getConfigurationSection("spawn");
        if (section == null) return null;

        SpawnPosition position = new SpawnPosition(section);
        return position.getCentered();
    }

    @Override
    public void nextPhase() {
        gamePhaseChanger.changeGamePhase(nextPhase);
    }

    @Override
    public List<String> scoreboardScores() {
        return config.getStringList("scoreboard.lobby");
    }
}
