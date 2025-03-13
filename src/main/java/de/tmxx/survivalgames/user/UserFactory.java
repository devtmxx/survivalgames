package de.tmxx.survivalgames.user;

import org.bukkit.entity.Player;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserFactory {
    User createUser(Player player);
}
