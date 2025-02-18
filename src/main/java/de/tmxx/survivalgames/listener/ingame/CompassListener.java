package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.CompassMeta;

/**
 * Project: survivalgames
 * 18.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@InGame
@DeathMatch
public class CompassListener implements Listener {
    private final UserRegistry registry;

    @Inject
    public CompassListener(UserRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;

        User user = registry.getUser(event.getPlayer());
        if (user == null) return;
        if (user.isSpectator()) return;
        if (event.getItem() == null) return;
        if (!event.getItem().getType().equals(Material.COMPASS)) return;

        Location targetLocation = findNearestPlayer(user);
        if (targetLocation == null) {
            user.sendMessage("compass.no-target-found");
        } else {
            user.getPlayer().setCompassTarget(targetLocation);
            user.sendMessage("compass.new-target", user.getPlayer().getLocation().distance(targetLocation));
        }
    }

    private Location findNearestPlayer(User user) {
        Location originLocation = user.getPlayer().getLocation();
        Location nearestLocation = null;
        double nearestDistance = 0;
        for (User found : registry.getUsers(UserState.PLAYING)) {
            if (nearestLocation == null) {
                nearestLocation = found.getPlayer().getLocation();
                nearestDistance = originLocation.distanceSquared(nearestLocation);
                continue;
            }

            Location foundLocation = found.getPlayer().getLocation();
            double foundDistance = originLocation.distanceSquared(foundLocation);
            if (foundDistance < nearestDistance) {
                nearestLocation = foundLocation;
                nearestDistance = foundDistance;
            }
        }

        return nearestLocation;
    }
}
