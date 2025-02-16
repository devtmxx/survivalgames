package de.tmxx.survivalgames.chest;

import de.tmxx.survivalgames.config.ChestItem;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class ChestTier {
    private static final Random RANDOM = new Random();
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

        int rand = RANDOM.nextInt(totalWeight) + 1;
        return cumulativeMap.ceilingEntry(rand).getValue();
    }
}
