package de.tmxx.survivalgames.inventory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class TeleportInventory implements InventoryGUI, Listener {
    private final UserRegistry registry;
    private final Map<Integer, User> slots = new HashMap<>();

    @Inject
    TeleportInventory(JavaPlugin plugin, UserRegistry registry, MapManager mapManager) {
        this.registry = registry;

        AtomicInteger slot = new AtomicInteger(0);
        registry.getUsers(UserState.PLAYING).forEach(user -> slots.put(slot.getAndIncrement(), user));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        User user = getUser(registry, event);
        if (user == null) return;

        User teleportTo = slots.get(event.getRawSlot());
        if (teleportTo == null) return;

        if (teleportTo.isSpectator()) {
            // the player has died in the meantime
            user.sendMessage("inventory.teleport.item.now-spectator");
        }

        user.getPlayer().teleport(teleportTo.getPlayer().getLocation());
    }

    @Override
    public void openInventory(User user) {
        Inventory inventory = Bukkit.createInventory(this, calculateSize(slots.size()), user.translate("inventory.teleport.name"));
        populateInventory(inventory, user);
        user.getPlayer().openInventory(inventory);
    }

    private void populateInventory(Inventory inventory, User user) {
        slots.forEach((slot, teleportTo) -> {
            ItemStack itemStack = ItemStack.of(Material.PLAYER_HEAD);
            itemStack.editMeta(meta -> {
                meta.displayName(user.translate("inventory.teleport.item.name", teleportTo.getName()));
                meta.lore(user.translateList("inventory.teleport.item.lore"));
            });
            inventory.setItem(slot, itemStack);
        });
    }
}
