package de.tmxx.survivalgames.user;

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
}
