package de.tmxx.survivalgames.item.impl;

import com.google.inject.Singleton;
import de.tmxx.survivalgames.item.ClickableItem;
import de.tmxx.survivalgames.item.ItemRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class ItemRegistryImpl implements ItemRegistry {
    private final Map<String, ClickableItem> items = new HashMap<>();

    @Override
    public void registerItem(ClickableItem item) {
        items.put(item.getId(), item);
    }

    @Override
    public ClickableItem getItem(String id) {
        return items.get(id);
    }
}
