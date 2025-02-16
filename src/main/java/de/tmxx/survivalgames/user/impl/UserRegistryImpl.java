package de.tmxx.survivalgames.user.impl;

import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import jakarta.inject.Singleton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class UserRegistryImpl implements UserRegistry {
    private static final Map<String, User> NAME_USER_MAP = new HashMap<>();
    private static final Map<UUID, User> UUID_USER_MAP = new HashMap<>();

    @Override
    public void registerUser(User user) {
        NAME_USER_MAP.put(user.getName().toLowerCase(), user);
        UUID_USER_MAP.put(user.getUniqueId(), user);
    }

    @Override
    public User getUser(UUID uniqueId) {
        return UUID_USER_MAP.get(uniqueId);
    }

    @Override
    public User getUser(String name) {
        return NAME_USER_MAP.get(name.toLowerCase());
    }

    @Override
    public User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    @Override
    public Collection<User> getUsers(UserState state) {
        return getOnlineUsers().stream().filter(user -> user.getState().equals(state)).toList();
    }

    @Override
    public Collection<User> getOnlineUsers() {
        List<User> online = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = getUser(player);
            if (user == null) continue;

            online.add(user);
        }
        return Collections.unmodifiableCollection(online);
    }

    @Override
    public void unregisterUser(User user) {
        NAME_USER_MAP.remove(user.getName().toLowerCase());
        UUID_USER_MAP.remove(user.getUniqueId());
    }
}
