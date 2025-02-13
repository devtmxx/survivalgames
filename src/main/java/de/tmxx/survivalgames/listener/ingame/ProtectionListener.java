package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.user.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = {
        GameState.IN_GAME,
        GameState.DEATH_MATCH
})
@RequiredArgsConstructor
public class ProtectionListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        // cancel for all blocks not listed under breakable blocks
        event.setCancelled(
                !plugin.getConfig().getStringList("blocks.breakable").contains(event.getBlock().getType().name())
        );
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null || user.isSpectator()) {
            event.setCancelled(true);
            return;
        }

        // cancel for all blocks not listed under placeable blocks
        event.setCancelled(
                !plugin.getConfig().getStringList("blocks.placeable").contains(event.getBlock().getType().name())
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
                !plugin.getConfig().getStringList("allowed-entities").contains(event.getEntity().getType().name())
        );
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;

        User user = User.getUser(player);
        if (user == null) return;

        // spectators are not allowed to change anything in the world
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null) return;

        // spectators are not allowed to drop items
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null) return;

        // spectators are not allowed to interact with the environment
        event.setCancelled(user.isSpectator());
    }
}
