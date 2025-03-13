package de.tmxx.survivalgames.user;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserRegistry {
    void registerUser(User user);
    User getUser(UUID uniqueId);
    User getUser(String name);
    User getUser(Player player);
    List<User> getUsers(UserState state);
    List<User> getOnlineUsers();
    void unregisterUser(User user);
}
