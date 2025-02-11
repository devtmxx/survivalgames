package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserState;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = {
        GameState.STARTING,
        GameState.IN_GAME,
        GameState.DEATH_MATCH
})
@RequiredArgsConstructor
public class ProtectionListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null || user.getState().equals(UserState.SPECTATING)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(
                plugin.getConfig().getStringList("blocks.breakable").contains(event.getBlock().getType().name())
        );
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null || user.getState().equals(UserState.SPECTATING)) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getConfig().getStringList("blocks.placeable").contains(event.getBlock().getType().name())) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType().equals(Material.TNT) && plugin.getConfig().getBoolean("auto-ignite-tnt", true)) {
            // spawn primed tnt
            event.getBlock().setType(Material.AIR);
            event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
        }
    }
}
