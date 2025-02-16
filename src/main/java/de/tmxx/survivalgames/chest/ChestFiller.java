package de.tmxx.survivalgames.chest;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ChestFiller {
    Inventory fill(Chest chest);
}
