package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.chest.ChestFiller;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

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
    private static final int DEFAULT_REFILL_TIME = 600; // 10 minutes
    private static final int INSANE_ATTACK_SPEED = 100;

    private final Game game;
    private final MapManager mapManager;
    private final ListenerRegistrar listenerRegistrar;
    private final UserBroadcaster broadcaster;
    private final FileConfiguration config;
    private final GamePhase nextPhase;
    private final ChestFiller chestFiller;
    private final UserRegistry registry;
    private final GamePhaseChanger gamePhaseChanger;

    @Inject
    InGamePhase(
            Game game,
            MapManager mapManager,
            ListenerRegistrar listenerRegistrar,
            UserBroadcaster broadcaster,
            @MainConfig FileConfiguration config,
            @DeathMatch GamePhase nextPhase,
            ChestFiller chestFiller,
            UserRegistry registry,
            GamePhaseChanger gamePhaseChanger
    ) {
        this.game = game;
        this.mapManager = mapManager;
        this.listenerRegistrar = listenerRegistrar;
        this.broadcaster = broadcaster;
        this.config = config;
        this.nextPhase = nextPhase;
        this.chestFiller = chestFiller;
        this.registry = registry;
        this.gamePhaseChanger = gamePhaseChanger;
    }

    @Override
    public void start() {
        game.resetTimer();
        listenerRegistrar.registerPhaseSpecific(InGame.class);
        broadcaster.broadcastScoreboardSetup();

        if (config.getBoolean("no-attack-cooldown", true)) {
            registry.getUsers(UserState.PLAYING).forEach(user -> {
                AttributeInstance attackSpeed = user.getPlayer().getAttribute(Attribute.ATTACK_SPEED);
                if (attackSpeed == null) return;

                // used to simulate pre 1.9 cooldown
                attackSpeed.setBaseValue(INSANE_ATTACK_SPEED);
            });
        }

        broadcaster.broadcast("timers.in-game.chat.start");
        broadcaster.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 2F);
    }

    @Override
    public void tick() {
        if (!game.isCounting()) return;

        broadcaster.broadcastScoreboardUpdate();

        tryChestRefill();
        tryShortenInGameTime();
        tryBroadcast();
    }

    @Override
    public void end() {
        broadcaster.broadcastScoreboardReset();
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
        gamePhaseChanger.changeGamePhase(nextPhase);
    }

    @Override
    public List<String> scoreboardScores() {
        return config.getStringList("scoreboard.in-game");
    }

    private void tryBroadcast() {
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

    private void tryChestRefill() {
        if (!config.getBoolean("chest.refill", true)) return;

        int refillTime = config.getInt("chest.refill-time", DEFAULT_REFILL_TIME);
        int secondsLeft = refillTime - game.secondsElapsed();
        if (secondsLeft < 0) return;

        boolean displayMinutes = secondsLeft > 60;
        int displayTime = displayMinutes ? secondsLeft / 60 : secondsLeft;

        // we don't want to broadcast 0 seconds because we have another message for that case
        if (shouldBroadcastTimeLeft(secondsLeft) && secondsLeft != 0) {
            broadcaster.broadcast(
                    "chest-refill.timer." + (displayMinutes ? "minutes" : "seconds") + "." + (displayTime == 1 ? "single" : "multiple"),
                    displayTime
            );
        }

        if (game.secondsElapsed() == refillTime) {
            chestFiller.reset();
            broadcaster.broadcast("chest-refill.refilled");
        }
    }

    private void tryShortenInGameTime() {
        if (!config.getBoolean("death-match.shorten-in-game-time", true)) return;

        int playersLeft = registry.getUsers(UserState.PLAYING).size();
        int shortenToSeconds = config.getInt("death-match.shorten-time", 30);
        if (playersLeft <= config.getInt("death-match.shorten-players", 2) && game.secondsLeft() > shortenToSeconds) {
            game.setTimer(countdownSeconds() - shortenToSeconds);
            broadcaster.broadcast("death-match-shorten-in-game-time");
        }
    }
}
