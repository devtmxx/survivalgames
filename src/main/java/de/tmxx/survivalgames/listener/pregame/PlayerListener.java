package de.tmxx.survivalgames.listener.pregame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.module.config.MaxPlayers;
import de.tmxx.survivalgames.module.config.MinPlayers;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserPreparer;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Lobby
public class PlayerListener implements Listener {
    private final int maxPlayers;
    private final int minPlayers;
    private final UserBroadcaster broadcaster;
    private final Game game;
    private final UserRegistry registry;
    private final UserPreparer preparer;

    @Inject
    PlayerListener(
            @MaxPlayers int maxPlayers,
            @MinPlayers int minPlayers,
            UserBroadcaster broadcaster,
            Game game,
            UserRegistry registry,
            UserPreparer preparer
    ) {
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.broadcaster = broadcaster;
        this.game = game;
        this.registry = registry;
        this.preparer = preparer;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        event.joinMessage(null);

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        broadcaster.broadcast("join", event.getPlayer().getName(), onlinePlayers, maxPlayers);

        if (onlinePlayers >= minPlayers) {
            // start the timer if there are enough players online
            game.startTimer();
        }

        preparer.prepareUserForLobby(user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        broadcaster.broadcast("quit", event.getPlayer().getName());

        // subtracting 1 because the leaving player still counts as an online player right now
        int onlinePlayers = Bukkit.getOnlinePlayers().size() - 1;

        if (onlinePlayers < minPlayers) {
            // stop and reset the timer if there are not enough players online
            game.stopTimer();
            game.resetTimer();
        }
    }
}
