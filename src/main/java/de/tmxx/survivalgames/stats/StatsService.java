package de.tmxx.survivalgames.stats;

import org.bukkit.OfflinePlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface StatsService {
    void prepare();
    Stats loadStats(OfflinePlayer player);
    Stats loadStats(String name);
    void persist(OfflinePlayer player, Stats stats);

    Map<UUID, Stats> UUID_MAP = new ConcurrentHashMap<>();
    Map<String, Stats> NAME_MAP = new ConcurrentHashMap<>();
}
