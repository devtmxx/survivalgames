package de.tmxx.survivalgames.listener.feature;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = {
        GameState.IN_GAME,
        GameState.DEATH_MATCH
})
@RequiredArgsConstructor
public class InstantTNTListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getType().equals(Material.TNT)) return;
        if (!plugin.getConfig().getBoolean("auto-ignite-tnt", true)) return;

        event.getBlock().setType(Material.AIR);
        event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
    }
}
