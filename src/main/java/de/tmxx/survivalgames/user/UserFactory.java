package de.tmxx.survivalgames.user;

import de.tmxx.survivalgames.user.impl.UserImpl;
import org.bukkit.entity.Player;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserFactory {
    UserImpl createUser(Player player);
}
