package de.tmxx.survivalgames.user.kicker;

import com.google.inject.Inject;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;

import java.util.List;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class RandomUserKicker implements UserKicker {
    private final UserRegistry registry;

    @Inject
    RandomUserKicker(UserRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean kick() {
        List<User> list = registry.getOnlineUsers().stream()
                .filter(user -> !user.getPlayer().hasPermission("survivalgames.priority"))
                .toList();

        if (list.isEmpty()) return false;

        User toKick = list.get((int) Math.floor(Math.random() * list.size()));
        toKick.getPlayer().kick(toKick.translate("kick.priority"));
        return true;
    }
}
