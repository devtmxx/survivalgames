package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.stats.Stats;
import de.tmxx.survivalgames.stats.StatsService;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class UserImpl implements User {
    private final I18n i18n;
    private final JavaPlugin plugin;
    private final UserRegistry registry;
    private final StatsService statsService;

    @Getter private final UUID uniqueId;
    @Getter private final String name;
    @Getter private final Player player;

    @Getter @Setter private UserState state = UserState.PLAYING;
    private boolean hasVoted = false;

    @Getter private Stats stats = null;
    private final Lock lock = new ReentrantLock();
    private final Set<Consumer<Stats>> whenLoaded = new HashSet<>();

    private User lastDamager = null;
    private long lastHitFromDamager = 0;

    @Inject
    UserImpl(I18n i18n, JavaPlugin plugin, UserRegistry registry, StatsService statsService, @Assisted Player player) {
        this.i18n = i18n;
        this.plugin = plugin;
        this.registry = registry;
        this.statsService = statsService;

        this.player = player;
        uniqueId = player.getUniqueId();
        name = player.getName();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            stats = statsService.loadStats(player);

            lock.lock();
            try {
                whenLoaded.forEach(consumer -> consumer.accept(stats));
                whenLoaded.clear();
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    public void retrieveStats(Consumer<Stats> whenLoaded) {
        lock.lock();
        try {
            if (stats == null) this.whenLoaded.add(whenLoaded);
            whenLoaded.accept(stats);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void saveStats() {
        if (Bukkit.isStopping()) {
            statsService.persist(player, stats);
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> statsService.persist(player, stats));
        }
    }

    @Override
    public void setSpectator() {
        state = UserState.SPECTATING;

        hideForPlayers();
        showForSpectators();
    }

    @Override
    public void sendMessage(String key, Object... args) {
        player.sendMessage(translate(key, args));
    }

    @Override
    public Component translate(String key, Object... args) {
        if (key == null) return Component.empty();
        return i18n.translate(player.locale(), key, args);
    }

    @Override
    public List<Component> translateList(String key, Object... args) {
        return i18n.translateList(player.locale(), key, args);
    }

    @Override
    public boolean isSpectator() {
        return state.equals(UserState.SPECTATING);
    }

    @Override
    public void vote(Map map) {
        if (hasVoted) {
            sendMessage("vote.already-voted");
            return;
        }

        hasVoted = true;
        map.castVote(uniqueId);
        sendMessage("vote.voted", map.getName());
    }

    @Override
    public void setDamager(User damager) {
        lastDamager = damager;
        lastHitFromDamager = System.currentTimeMillis();
    }

    @Override
    public User getKiller() {
        if (player.getKiller() != null) {
            return registry.getUser(player.getKiller());
        }

        // the last damager counts as killer if their last hit is less than 5 seconds ago
        if (System.currentTimeMillis() - lastHitFromDamager > TimeUnit.SECONDS.toMillis(5)) return null;

        return lastDamager;
    }

    @Override
    public void showForAllPlayers() {
        registry.getOnlineUsers().forEach(user -> user.getPlayer().showPlayer(plugin, player));
    }

    private void hideForPlayers() {
        registry.getUsers(UserState.PLAYING).forEach(user -> user.getPlayer().hidePlayer(plugin, player));
    }

    private void showForSpectators() {
        registry.getUsers(UserState.SPECTATING).forEach(user -> user.getPlayer().showPlayer(plugin, player));
    }
}
