package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@InGame
@DeathMatch
public class DamageListener implements Listener {
    private final UserRegistry registry;

    @Inject
    public DamageListener(UserRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // we only care about players. all other mobs can take damage throughout the whole game
        if (!(event.getEntity() instanceof Player player)) return;

        // the user should always be defined. if that's not the case we just ignore the outcome so players can complain
        // about the error, and it may be easier to fix (if this ever happens at all)
        User user = registry.getUser(player);
        if (user == null) return;

        // cancel damage for spectators
        event.setCancelled(user.isSpectator());
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        User target = null;
        User damager = null;

        if (event.getEntity() instanceof Player player) target = registry.getUser(player);
        if (event.getDamager() instanceof Player player) damager = registry.getUser(player);
        if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player player) damager = registry.getUser(player);

        // cancel damage if either the target or the damager are spectators
        event.setCancelled(
                (target != null && target.isSpectator()) ||
                        (damager != null && damager.isSpectator())
        );
    }
}
