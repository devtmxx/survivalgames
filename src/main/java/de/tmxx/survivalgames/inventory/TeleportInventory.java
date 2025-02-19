package de.tmxx.survivalgames.inventory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import de.tmxx.survivalgames.user.UserState;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class TeleportInventory implements InventoryGUI, Listener {
    private final UserRegistry registry;
    private final User user;
    private final Map<Integer, User> slots = new HashMap<>();

    @Inject
    TeleportInventory(JavaPlugin plugin, UserRegistry registry, @Assisted User user) {
        this.registry = registry;
        this.user = user;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!user.getUniqueId().equals(event.getWhoClicked().getUniqueId())) return;

        User teleportTo = slots.get(event.getRawSlot());
        if (teleportTo == null) return;

        if (teleportTo.isSpectator()) {
            // the player has died in the meantime
            user.sendMessage("inventory.teleport.item.now-spectator");
            event.getWhoClicked().closeInventory();
            return;
        }

        user.getPlayer().teleport(teleportTo.getPlayer().getLocation());
        event.getWhoClicked().closeInventory();
    }

    @Override
    public void openInventory(User ignored) {
        createSlots();

        Inventory inventory = Bukkit.createInventory(this, calculateSize(slots.size()), user.translate("inventory.teleport.name"));
        populateInventory(inventory);
        user.getPlayer().openInventory(inventory);
    }

    private void createSlots() {
        slots.clear();

        AtomicInteger currentSlot = new AtomicInteger(0);
        registry.getUsers(UserState.PLAYING).forEach(playing -> slots.put(currentSlot.getAndIncrement(), playing));
    }

    private void populateInventory(Inventory inventory) {
        slots.forEach((slot, teleportTo) -> {
            ItemStack itemStack = ItemStack.of(Material.PLAYER_HEAD);
            itemStack.editMeta(meta -> {
                meta.displayName(user.translate("inventory.teleport.item.name", teleportTo.getName()).decoration(TextDecoration.ITALIC, false));
                meta.lore(user.translateList("inventory.teleport.item.lore"));
            });
            inventory.setItem(slot, itemStack);
        });
    }

    /* ------------- unregister events ------------------ */

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!user.getUniqueId().equals(event.getPlayer().getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!user.getUniqueId().equals(event.getPlayer().getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }
}
