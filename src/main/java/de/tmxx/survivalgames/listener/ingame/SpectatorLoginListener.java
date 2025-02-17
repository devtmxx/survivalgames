package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.Ending;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.module.game.phase.Starting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Starting
@InGame
@DeathMatch
@Ending
public class SpectatorLoginListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        // allow all players to join as spectators
        event.allow();
    }
}
