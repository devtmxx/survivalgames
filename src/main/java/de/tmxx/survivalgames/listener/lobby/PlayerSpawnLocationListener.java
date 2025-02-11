package de.tmxx.survivalgames.listener.lobby;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.GameState;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = { GameState.LOBBY })
@RequiredArgsConstructor
public class PlayerSpawnLocationListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        SpawnPosition spawn = plugin.getConfig().getSerializable("spawn", SpawnPosition.class);
        if (spawn == null) return;

        event.setSpawnLocation(spawn.getCentered());
    }
}
