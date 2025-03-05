package de.tmxx.survivalgames.listener.general;

import com.google.inject.Inject;
import de.tmxx.survivalgames.listener.RegisterAlways;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.scoreboard.GameScoreboard;
import de.tmxx.survivalgames.scoreboard.ScoreboardFactory;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserFactory;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
    private final ScoreboardFactory scoreboardFactory;
    private final boolean scoreboardEnabled;

    @Inject
    UserListener(
            UserRegistry registry,
            UserFactory factory,
            @MainConfig FileConfiguration config,
            ScoreboardFactory scoreboardFactory
    ) {
        this.registry = registry;
        this.factory = factory;
        this.scoreboardFactory = scoreboardFactory;
        scoreboardEnabled = config.getBoolean("scoreboard.enabled", true);
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

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        if (scoreboardEnabled) {
            GameScoreboard scoreboard = scoreboardFactory.createScoreboard(user);
            scoreboard.setup();
            user.setScoreboard(scoreboard);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        user.saveStats();
        registry.unregisterUser(user);
    }
}
