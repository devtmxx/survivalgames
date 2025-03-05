package de.tmxx.survivalgames.user;

import org.bukkit.Sound;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserBroadcaster {
    void broadcast(String key, Object... args);
    void broadcastActionBar(String key, Object... args);
    void broadcastSound(Sound sound, float volume, float pitch);
    void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);
    void broadcastScoreboardSetup();
    void broadcastScoreboardUpdate();
    void broadcastScoreboardReset();
}
