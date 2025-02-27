package de.tmxx.survivalgames.inventory;

import de.tmxx.survivalgames.module.game.interactable.Teleport;
import de.tmxx.survivalgames.user.User;

/**
 * Project: survivalgames
 * 19.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface InventoryFactory {
    @Teleport InventoryGUI createTeleporterInventory(User user);
}
