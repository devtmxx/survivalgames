package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserKicker;
import de.tmxx.survivalgames.user.UserRegistry;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class LastUserKicker implements UserKicker {
    private final UserRegistry registry;

    @Inject
    LastUserKicker(UserRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean kick() {
        User toKick = registry.getOnlineUsers().stream()
                .filter(user -> !user.getPlayer().hasPermission("survivalgames.priority"))
                .toList()
                .getLast();

        if (toKick == null) return false;

        toKick.getPlayer().kick(toKick.translate("kick.priority"));
        return true;
    }
}
