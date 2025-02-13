package de.tmxx.survivalgames.listener.pregame;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.game.phase.GamePhase;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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
@AutoRegister(value = RegisterState.GAME, states = { GameState.LOBBY })
@RequiredArgsConstructor
public class PlayerListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minPlayers = plugin.getMinPlayers();
        int maxPlayers = plugin.getMaxPlayers();
        plugin.broadcast("join", event.getPlayer().getName(), onlinePlayers, maxPlayers);

        if (onlinePlayers >= minPlayers) {
            // start the timer if there are enough players online
            plugin.getGame().getCurrentPhase().setCounting(true);
        }

        preparePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        plugin.broadcast("quit", event.getPlayer().getName());

        int minPlayers = plugin.getMinPlayers();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        if (onlinePlayers < minPlayers) {
            // stop and reset the timer if there are not enough players online
            GamePhase phase = plugin.getGame().getCurrentPhase();
            phase.setCounting(false);
            phase.reset();
        }
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        SpawnPosition spawn = plugin.getConfig().getSerializable("spawn", SpawnPosition.class);
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
