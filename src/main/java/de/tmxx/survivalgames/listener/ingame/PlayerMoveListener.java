package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.module.game.phase.Starting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Starting
public class PlayerMoveListener implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // teleport the player to where he came from if he changed block positions while the game is running the
        // starting countdown
        if (event.hasChangedBlock()) event.getPlayer().teleport(event.getFrom());
    }
}
