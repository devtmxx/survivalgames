package de.tmxx.survivalgames.inventory;

import de.tmxx.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface InventoryGUI extends InventoryHolder {
    void openInventory(User user);

    default @NotNull Inventory getInventory() {
        // we don't need this method to take advantage of the inventory holder. we use openInventory(User) to open
        // this inventory for a player using its specific locale.
        return Bukkit.createInventory(null, 0);
    }
}
