package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserRegistry;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.time.Duration;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class UserBroadcasterImpl implements UserBroadcaster {
    private final UserRegistry registry;
    private final boolean scoreboardEnabled;

    @Inject
    UserBroadcasterImpl(UserRegistry registry, @MainConfig FileConfiguration config) {
        this.registry = registry;
        scoreboardEnabled = config.getBoolean("scoreboard.enabled", true);
    }

    public void broadcast(String key, Object... args) {
        registry.getOnlineUsers().forEach(user -> user.sendMessage(key, args));
    }

    @Override
    public void broadcastActionBar(String key, Object... args) {
        registry.getOnlineUsers().forEach(user -> user.getPlayer().sendActionBar(user.translate(key, args)));
    }

    @Override
    public void broadcastSound(Sound sound, float volume, float pitch) {
        registry.getOnlineUsers().forEach(user -> user.getPlayer().playSound(user.getPlayer().getLocation(), sound, volume, pitch));
    }

    @Override
    public void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        registry.getOnlineUsers().forEach(user -> user.getPlayer().showTitle(
                Title.title(user.translate(title), user.translate(subtitle), Title.Times.times(
                        Duration.ofMillis(fadeIn),
                        Duration.ofMillis(stay),
                        Duration.ofMillis(fadeOut)
                ))
        ));
    }

    @Override
    public void broadcastScoreboardSetup() {
        if (!scoreboardEnabled) return;
        registry.getOnlineUsers().forEach(user -> user.getScoreboard().setup());
    }

    @Override
    public void broadcastScoreboardUpdate() {
        if (!scoreboardEnabled) return;
        registry.getOnlineUsers().forEach(user -> user.getScoreboard().update());
    }

    @Override
    public void broadcastScoreboardReset() {
        if (!scoreboardEnabled) return;
        registry.getOnlineUsers().forEach(user -> user.getScoreboard().reset());
    }
}
