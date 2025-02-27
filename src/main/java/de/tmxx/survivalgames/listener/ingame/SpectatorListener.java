package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.Ending;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.module.game.phase.Starting;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserPreparer;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Starting
@InGame
@DeathMatch
@Ending
public class SpectatorListener implements Listener {
    private final UserRegistry registry;
    private final UserPreparer preparer;

    @Inject
    SpectatorListener(UserRegistry registry, UserPreparer preparer) {
        this.registry = registry;
        this.preparer = preparer;
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;

        User user = registry.getUser(player);
        if (user == null) return;

        // spectators should never be targeted by mobs
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        // spectators are not allowed to drop items
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        User user = registry.getUser(player);
        if (user == null) return;

        // spectators are not allowed to pickup items
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!(event.getRemover() instanceof Player player)) return;

        User user = registry.getUser(player);
        if (user == null) return;

        // spectators are not allowed to change anything in the world
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        // spectators are not allowed to interact with the environment
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        // prepare the user to be a spectator
        preparer.prepareUserForSpectator(user);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        // prepare the user to be a spectator
        preparer.prepareUserForSpectator(user);
    }
}
