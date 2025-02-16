package de.tmxx.survivalgames.listener.feature;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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
@InGame
@DeathMatch
public class InstantTNTListener implements Listener {
    private final FileConfiguration config;

    @Inject
    public InstantTNTListener(@MainConfig FileConfiguration config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getType().equals(Material.TNT)) return;
        if (!config.getBoolean("auto-ignite-tnt", true)) return;

        event.getBlock().setType(Material.AIR);
        event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
    }
}
