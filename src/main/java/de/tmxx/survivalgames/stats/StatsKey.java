package de.tmxx.survivalgames.stats;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Getter
public enum StatsKey {
    KILLS("stat.kills", "kills"),
    DEATHS("stat.deaths", "deaths"),
    WINS("stat.wins", "wins"),
    GAMES_PLAYED("stat.games-played", "games_played");

    private final String nameKey;
    private final String storageKey;

    StatsKey(String nameKey, String storageKey) {
        this.nameKey = nameKey;
        this.storageKey = storageKey;
    }

    private static final Map<String, StatsKey> BY_KEY = new HashMap<>();

    public static StatsKey getByKey(String key) {
        return BY_KEY.get(key);
    }

    static {
        for (StatsKey key : StatsKey.values()) {
            BY_KEY.put(key.storageKey, key);
        }
    }
}
