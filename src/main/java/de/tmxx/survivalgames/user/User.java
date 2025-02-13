package de.tmxx.survivalgames.user;

import de.tmxx.survivalgames.SurvivalGames;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class User {
    private static final Map<String, User> NAME_USER_MAP = new HashMap<>();
    private static final Map<UUID, User> UUID_USER_MAP = new HashMap<>();

    public static User getUser(UUID uniqueId) {
        return UUID_USER_MAP.get(uniqueId);
    }

    public static User getUser(String name) {
        return NAME_USER_MAP.get(name.toLowerCase());
    }

    public static User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public static Collection<User> getUsers(UserState state) {
        return getOnlineUsers().stream().filter(user -> user.getState().equals(state)).toList();
    }

    public static Collection<User> getOnlineUsers() {
        return Bukkit.getOnlinePlayers().stream().map(User::getUser).toList();
    }

    private final SurvivalGames plugin;
    @Getter private final UUID uniqueId;
    @Getter private final String name;
    @Getter private final Player player;

    @Getter @Setter private UserState state = UserState.PLAYING;

    public User(SurvivalGames plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        uniqueId = player.getUniqueId();
        name = player.getName();

        NAME_USER_MAP.put(name.toLowerCase(), this);
        UUID_USER_MAP.put(uniqueId, this);
    }

    public void sendMessage(String key, Object... args) {
        player.sendMessage(translate(key, args));
    }

    public Component translate(String key, Object... args) {
        return plugin.getI18n().translate(player.locale(), key, args);
    }

    public List<Component> translateList(String key, Object... args) {
        return plugin.getI18n().translateList(player.locale(), key, args);
    }

    public boolean isSpectator() {
        return state.equals(UserState.SPECTATING);
    }

    public void setSpectator() {
        state = UserState.SPECTATING;

        // clear inventory and allow spectators to fly
        player.getInventory().clear();
        player.setAllowFlight(true);
        player.setFlying(true);

        // TODO: give teleporter item

        // give invisibility effect to spectating players, so other spectators see this player half-transparent
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));

        User.getOnlineUsers().forEach(user -> {
            if (user.isSpectator()) {
                // spectators should see each other
                user.getPlayer().showPlayer(plugin, player);
                player.showPlayer(plugin, user.getPlayer());
            } else {
                // players should not see spectators, but the other way around
                user.getPlayer().hidePlayer(plugin, player);
                player.showPlayer(plugin, user.getPlayer());
            }
        });

        // TODO: set not collidable and player visibility in scoreboard
    }

    public void remove() {
        NAME_USER_MAP.remove(name.toLowerCase());
        UUID_USER_MAP.remove(uniqueId);
    }
}
