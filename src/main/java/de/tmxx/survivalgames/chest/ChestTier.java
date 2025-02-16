package de.tmxx.survivalgames.chest;

import de.tmxx.survivalgames.config.ChestItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class ChestTier {
    private final NavigableMap<Integer, ChestItem> cumulativeMap = new TreeMap<>();
    private int totalWeight = 0;

    public ChestTier(List<Map<?, ?>> data) {
        data.forEach(map -> {
            ChestItem item = new ChestItem(map);
            totalWeight += item.getWeight();
            cumulativeMap.put(totalWeight, item);
        });
    }

    public @Nullable ChestItem getRandomItem() {
        // there are no items in this tier if the total weight is zero
        if (totalWeight == 0) return null;

        int rand = (int) (Math.random() * totalWeight);
        return cumulativeMap.ceilingEntry(rand).getValue();
    }
}
