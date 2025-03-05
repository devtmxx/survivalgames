package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.Ending;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class DeathMatchPhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 300;

    private final Game game;
    private final UserRegistry registry;
    private final UserBroadcaster broadcaster;
    private final FileConfiguration config;
    private final GamePhase nextPhase;
    private final ListenerRegistrar listenerRegistrar;
    private final GamePhaseChanger gamePhaseChanger;

    @Inject
    DeathMatchPhase(
            Game game,
            UserRegistry registry,
            UserBroadcaster broadcaster,
            @MainConfig FileConfiguration config,
            @Ending GamePhase nextPhase,
            ListenerRegistrar listenerRegistrar,
            GamePhaseChanger gamePhaseChanger
    ) {
        this.game = game;
        this.registry = registry;
        this.broadcaster = broadcaster;
        this.config = config;
        this.nextPhase = nextPhase;
        this.listenerRegistrar = listenerRegistrar;
        this.gamePhaseChanger = gamePhaseChanger;
    }

    @Override
    public void start() {
        game.resetTimer();
        listenerRegistrar.registerPhaseSpecific(DeathMatch.class);

        teleportPlayers();
        teleportSpectators();
    }

    @Override
    public void tick() {
        if (!game.isCounting()) return;

        int secondsLeft = game.secondsLeft();
        if (!shouldBroadcastTimeLeft(secondsLeft)) return;

        boolean displayMinutes = secondsLeft > 60;
        int displayTime = displayMinutes ? secondsLeft / 60 : secondsLeft;

        broadcaster.broadcast(
                "timers.death-match.chat." + (displayMinutes ? "minutes" : "seconds") + "." + (displayTime == 1 ? "single" : "multiple"),
                displayTime
        );
        broadcaster.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1F, 1F);
    }

    @Override
    public void end() {
        listenerRegistrar.unregisterPhaseSpecific(DeathMatch.class);
    }

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.death-match", DEFAULT_COUNTDOWN_SECONDS);
    }

    @Override
    public Location spawnLocation() {
        ConfigurationSection section = config.getConfigurationSection("deathmatch-spectator");
        if (section == null) return null;

        SpawnPosition position = new SpawnPosition(section);
        return position.getCentered();
    }

    @Override
    public void nextPhase() {
        gamePhaseChanger.changeGamePhase(nextPhase);
    }

    private void teleportPlayers() {
        AtomicInteger index = new AtomicInteger(0);
        List<SpawnPosition> spawns = SpawnPosition.fromList(config.getMapList("deathmatch-spawns"));
        registry.getUsers(UserState.PLAYING).forEach(user -> {
            if (spawns.size() <= index.get()) {
                // kick the player if there is no spawn found (should not happen)
                user.getPlayer().kick(user.translate("kick.no-spawn"));
                return;
            }

            user.getPlayer().teleport(spawns.get(index.getAndIncrement()).getCentered());
        });
    }

    private void teleportSpectators() {
        Location spectatorSpawn = spawnLocation();
        registry.getUsers(UserState.SPECTATING).forEach(user -> {
            if (spectatorSpawn == null) {
                // kick the player if there is no spawn found (should not happen)
                user.getPlayer().kick(user.translate("kick.no-spawn"));
                return;
            }

            user.getPlayer().teleport(spectatorSpawn);
        });
    }
}
