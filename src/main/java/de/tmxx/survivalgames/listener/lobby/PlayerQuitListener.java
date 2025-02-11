package de.tmxx.survivalgames.listener.lobby;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.game.phase.GamePhase;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = { GameState.LOBBY })
@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        plugin.broadcast("quit", event.getPlayer().getName());

        int minPlayers = plugin.getMinPlayers();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        if (onlinePlayers < minPlayers) {
            GamePhase phase = plugin.getGame().getCurrentPhase();
            phase.setCounting(false);
            phase.reset();
        }
    }
}
