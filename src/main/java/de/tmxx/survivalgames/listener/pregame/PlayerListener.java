package de.tmxx.survivalgames.listener.pregame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.config.MaxPlayers;
import de.tmxx.survivalgames.module.config.MinPlayers;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.user.UserBroadcaster;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

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
    private final FileConfiguration config;

    @Inject
    public PlayerListener(
            @MaxPlayers int maxPlayers,
            @MinPlayers int minPlayers,
            UserBroadcaster broadcaster,
            Game game,
            @MainConfig FileConfiguration config
    ) {
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.broadcaster = broadcaster;
        this.game = game;
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        broadcaster.broadcast("join", event.getPlayer().getName(), onlinePlayers, maxPlayers);

        if (onlinePlayers >= minPlayers) {
            // start the timer if there are enough players online
            game.startTimer();
        }

        preparePlayer(event.getPlayer());
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

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        SpawnPosition spawn = config.getSerializable("spawn", SpawnPosition.class);
        if (spawn == null) return;

        // all players should spawn at the lobby spawn (if it exists)
        event.setSpawnLocation(spawn.getCentered());
    }

    private void preparePlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.updateInventory();
    }
}
