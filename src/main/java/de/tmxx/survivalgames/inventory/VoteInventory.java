package de.tmxx.survivalgames.inventory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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
    private final MapManager mapManager;
    private final java.util.Map<Integer, Map> slots = new HashMap<>();

    private boolean built = false;

    @Inject
    VoteInventory(JavaPlugin plugin, UserRegistry registry, MapManager mapManager) {
        this.registry = registry;
        this.mapManager = mapManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        User user = getUser(registry, event);
        if (user == null) return;

        Map map = slots.get(event.getRawSlot());
        if (map == null) return;

        user.vote(map);
        event.getWhoClicked().closeInventory();
    }

    @Override
    public void openInventory(User user) {
        createSlots();

        Inventory inventory = Bukkit.createInventory(this, calculateSize(slots.size()), user.translate("inventory.vote.name"));
        populateInventory(inventory, user);
        user.getPlayer().openInventory(inventory);
    }

    private void createSlots() {
        if (built) return;
        built = true;

        AtomicInteger slot = new AtomicInteger(0);
        mapManager.getUsableMaps().forEach(map -> slots.put(slot.getAndIncrement(), map));
    }

    private void populateInventory(Inventory inventory, User user) {
        slots.forEach((slot, map) -> {
            ItemStack itemStack = ItemStack.of(Material.MAP);
            itemStack.editMeta(meta -> {
                meta.displayName(user.translate("inventory.vote.item.name", map.getName()).decoration(TextDecoration.ITALIC, false));

                List<Component> lore = new ArrayList<>(user.translateList("inventory.vote.item.lore", map.getAuthor(), map.getVotes()));
                if (map.hasVoted(user.getUniqueId())) {
                    lore.addAll(user.translateList("inventory.vote.item.voted"));
                }

                meta.lore(lore);
            });
            inventory.setItem(slot, itemStack);
        });
    }
}
