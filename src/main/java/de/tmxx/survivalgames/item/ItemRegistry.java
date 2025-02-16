package de.tmxx.survivalgames.item;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ItemRegistry {
    void registerItem(ClickableItem item);
    ClickableItem getItem(String id);
}
