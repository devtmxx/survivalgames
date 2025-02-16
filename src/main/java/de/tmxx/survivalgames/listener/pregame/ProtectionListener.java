package de.tmxx.survivalgames.listener.pregame;

import de.tmxx.survivalgames.module.game.phase.Ending;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.module.game.phase.Starting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
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
@Lobby
@Starting
@Ending
public class ProtectionListener implements Listener {
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        // no world manipulation during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // no world manipulation during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // no damage during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // no damage during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        // no hunger during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // do not spawn mobs during the "peaceful" phase
        event.setCancelled(true);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        // do not allow hangings to be broken by anything during the "peaceful" phases
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        // prevent all item drops outside the game
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // prevent all player interactions with the environment
        event.setCancelled(true);
    }
}
