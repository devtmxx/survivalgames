package de.tmxx.survivalgames.listener.general;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.listener.RegisterAlways;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class PlayerSpawnLocationListener implements Listener {
    private final GamePhaseChanger gamePhaseChanger;

    @Inject
    PlayerSpawnLocationListener(GamePhaseChanger gamePhaseChanger) {
        this.gamePhaseChanger = gamePhaseChanger;
    }

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        Location spawnLocation = getSpawnLocation();
        if (spawnLocation == null) return;
        event.setSpawnLocation(spawnLocation);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Location spawnLocation = getSpawnLocation();
        if (spawnLocation == null) return;
        event.setRespawnLocation(spawnLocation);
    }

    private Location getSpawnLocation() {
        GamePhase phase = gamePhaseChanger.currentPhase();
        if (phase == null) return null;
        return phase.spawnLocation();
    }
}
