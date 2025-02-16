package de.tmxx.survivalgames.chest;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * <p>
 *     Chest fillers are used - as the name already tells - to fill chest inventories with items.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ChestFiller {
    /**
     * Fills the specified chest with items and returns the chests inventory.
     *
     * @param chest the chest to fill
     * @return the inventory
     */
    Inventory fill(Chest chest);

    /**
     * Reset all chest inventories.
     */
    void reset();
}
