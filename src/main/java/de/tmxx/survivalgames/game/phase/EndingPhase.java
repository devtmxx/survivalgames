package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.Ending;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserPreparer;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.Bukkit;
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
public class EndingPhase implements GamePhase {
    private static final int DEFAULT_COUNTDOWN_SECONDS = 20;

    private final Game game;
    private final FileConfiguration config;
    private final ListenerRegistrar listenerRegistrar;
    private final UserBroadcaster broadcaster;
    private final UserRegistry registry;
    private final UserPreparer preparer;

    @Inject
    EndingPhase(
            Game game,
            @MainConfig FileConfiguration config,
            ListenerRegistrar listenerRegistrar,
            UserBroadcaster broadcaster,
            UserRegistry registry,
            UserPreparer preparer
    ) {
        this.game = game;
        this.config = config;
        this.listenerRegistrar = listenerRegistrar;
        this.broadcaster = broadcaster;
        this.registry = registry;
        this.preparer = preparer;
    }

    @Override
    public void start() {
        game.resetTimer();
        listenerRegistrar.registerPhaseSpecific(Ending.class);

        Location spawnLocation = spawnLocation();
        registry.getOnlineUsers().forEach(user -> {
            user.getPlayer().teleport(spawnLocation);
            preparer.prepareUserForEnding(user);
        });

        User winner = game.winner();
        if (winner == null) {
            broadcaster.broadcast("end.no-winner");
        } else {
            broadcaster.broadcast("end.winner", winner.getName());
        }
    }

    @Override
    public void tick() {
        if (!game.isCounting()) return;

        int timeLeft = game.secondsLeft();
        if (shouldBroadcastTimeLeft(timeLeft)) {
            broadcaster.broadcast("timers.ending.chat." + (timeLeft == 1 ? "single" : "multiple"), timeLeft);
            broadcaster.broadcastSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 1F);
        }
    }

    @Override
    public void end() {}

    @Override
    public int countdownSeconds() {
        return config.getInt("timers.ending", DEFAULT_COUNTDOWN_SECONDS);
    }

    @Override
    public Location spawnLocation() {
        SpawnPosition position = config.getSerializable("spawn", SpawnPosition.class);
        if (position == null) return null;

        return position.getCentered();
    }

    @Override
    public void nextPhase() {
        Bukkit.shutdown();
    }
}
