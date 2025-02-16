package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.user.UserBroadcaster;
import de.tmxx.survivalgames.user.UserRegistry;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class UserBroadcasterImpl implements UserBroadcaster {
    private final UserRegistry registry;

    @Inject
    public UserBroadcasterImpl(UserRegistry registry) {
        this.registry = registry;
    }

    public void broadcast(String key, Object... args) {
        registry.getOnlineUsers().forEach(user -> user.sendMessage(key, args));
    }

    @Override
    public void broadcastActionBar(String key, Object... args) {
        registry.getOnlineUsers().forEach(user -> user.getPlayer().sendActionBar(user.translate(key, args)));
    }
}
