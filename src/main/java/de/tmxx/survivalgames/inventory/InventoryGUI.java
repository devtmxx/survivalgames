package de.tmxx.survivalgames.inventory;

import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    default int calculateSize(int target) {
        int base = 9;
        int mod = target % base;
        return Math.max(mod == 0 ? target : target + base - mod, base);
    }

    default User getUser(UserRegistry registry, InventoryClickEvent event) {
        if (event.getInventory().getHolder(false) != this) return null;
        if (!(event.getWhoClicked() instanceof Player player)) return null;
        event.setResult(Event.Result.DENY);

        return registry.getUser(player);
    }
}
