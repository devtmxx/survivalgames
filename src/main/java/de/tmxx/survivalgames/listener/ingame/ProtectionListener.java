package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@InGame
@DeathMatch
public class ProtectionListener implements Listener {
    private final UserRegistry registry;
    private final FileConfiguration config;

    @Inject
    ProtectionListener(UserRegistry registry, @MainConfig FileConfiguration config) {
        this.registry = registry;
        this.config = config;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        // cancel for all blocks not listed under breakable blocks
        event.setCancelled(
                !config.getStringList("blocks.breakable").contains(event.getBlock().getType().name())
        );
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        // cancel for all blocks not listed under placeable blocks
        event.setCancelled(
                !config.getStringList("blocks.placeable").contains(event.getBlock().getType().name())
        );
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // explosions should make damage but not destroy the world
        event.blockList().clear();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // only spawn allowed entities
        event.setCancelled(
                !config.getStringList("allowed-entities").contains(event.getEntity().getType().name())
        );
    }
}
