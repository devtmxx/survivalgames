package de.tmxx.survivalgames.inventory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
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
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class VoteInventory implements InventoryGUI, Listener {
    private final UserRegistry registry;
    private final java.util.Map<Integer, Map> slots = new HashMap<>();

    @Inject
    VoteInventory(JavaPlugin plugin, UserRegistry registry, MapManager mapManager) {
        this.registry = registry;

        AtomicInteger slot = new AtomicInteger(0);
        mapManager.getUsableMaps().forEach(map -> slots.put(slot.getAndIncrement(), map));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder(false) != this) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;
        event.setResult(Event.Result.DENY);

        User user = registry.getUser(player);
        if (user == null) return;

        Map map = slots.get(event.getRawSlot());
        if (map == null) return;

        user.vote(map);
    }

    @Override
    public void openInventory(User user) {
        Inventory inventory = Bukkit.createInventory(this, calculateSize(), user.translate("inventory.vote.name"));
        populateInventory(inventory, user);
        user.getPlayer().openInventory(inventory);
    }

    private int calculateSize() {
        int size = slots.size();

        int base = 9;
        int rest = size % base;
        int rows = size / base;

        return rows * base + rest == 0 ? 0 : base - rest;
    }

    private void populateInventory(Inventory inventory, User user) {
        slots.forEach((slot, map) -> {
            ItemStack itemStack = ItemStack.of(Material.MAP);
            itemStack.editMeta(meta -> {
                meta.displayName(user.translate("inventory.vote.item.name", map.getName()));

                List<Component> lore = user.translateList("inventory.vote.item.lore", map.getAuthor(), map.getVotes());
                if (map.hasVoted(user.getUniqueId())) {
                    lore.addAll(user.translateList("inventory.vote.item.voted"));
                }

                meta.lore(lore);
            });
            inventory.setItem(slot, itemStack);
        });
    }
}
