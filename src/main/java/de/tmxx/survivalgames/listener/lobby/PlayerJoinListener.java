package de.tmxx.survivalgames.listener.lobby;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = { GameState.LOBBY })
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);

        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minPlayers = plugin.getMinPlayers();
        int maxPlayers = plugin.getMaxPlayers();
        plugin.broadcast("join", event.getPlayer().getName(), onlinePlayers, maxPlayers);

        if (onlinePlayers >= minPlayers) {
            plugin.getGame().getCurrentPhase().setCounting(true);
        }

        preparePlayer(event.getPlayer());
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
