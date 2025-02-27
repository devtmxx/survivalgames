package de.tmxx.survivalgames.stats;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class Stats {
    private final Map<StatsKey, Integer> values = new HashMap<>();

    public void add(StatsKey key, int value) {
        values.put(key, values.getOrDefault(key, 0) + value);
    }

    public int get(StatsKey key) {
        return values.getOrDefault(key, 0);
    }
}
