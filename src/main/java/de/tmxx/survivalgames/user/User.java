package de.tmxx.survivalgames.user;

import de.tmxx.survivalgames.map.Map;
import net.kyori.adventure.text.Component;
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
public interface User {
    Player getPlayer();
    String getName();
    UUID getUniqueId();
    UserState getState();
    void sendMessage(String key, Object... args);
    Component translate(String key, Object... args);
    List<Component> translateList(String key, Object... args);
    boolean isSpectator();
    void vote(Map map);
}
