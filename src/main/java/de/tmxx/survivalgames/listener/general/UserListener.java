package de.tmxx.survivalgames.listener.general;

import com.google.inject.Inject;
import de.tmxx.survivalgames.listener.RegisterAlways;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserFactory;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class UserListener implements Listener {
    private final UserRegistry registry;
    private final UserFactory factory;

    @Inject
    UserListener(UserRegistry registry, UserFactory factory) {
        this.registry = registry;
        this.factory = factory;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        User user = factory.createUser(event.getPlayer());
        registry.registerUser(user);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void afterPlayerLogin(PlayerLoginEvent event) {
        if (event.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) return;

        // unregister the user again if the login is disallowed
        User user = registry.getUser(event.getPlayer());
        registry.unregisterUser(user);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        registry.unregisterUser(user);
    }
}
