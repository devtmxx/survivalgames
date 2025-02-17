package de.tmxx.survivalgames.listener.general;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
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
    private final Game game;

    @Inject
    PlayerSpawnLocationListener(Game game) {
        this.game = game;
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
        GamePhase phase = game.currentPhase();
        if (phase == null) return null;
        return phase.spawnLocation();
    }
}
