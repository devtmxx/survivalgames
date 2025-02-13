package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.user.User;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = {
        GameState.IN_GAME,
        GameState.DEATH_MATCH
})
@RequiredArgsConstructor
public class ChestListener implements Listener {
    private static final Map<Location, Inventory> INVENTORIES = new HashMap<>();
    private final SurvivalGames plugin;

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChestOpen(PlayerInteractEvent event) {
        User user = User.getUser(event.getPlayer());
        if (user == null) return;

        // ignore all interactions where players do not right-click a block
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        // the clicked block will never be null since the action suggests, that a block has been clicked, but this
        // if-clause will prevent IntelliJ from warning about a possible NPE
        if (event.getClickedBlock() == null) return;
        if (!event.getClickedBlock().getType().equals(Material.CHEST)) return;

        Inventory inventory = getInventory(event.getClickedBlock());
    }

    private Inventory getInventory(Block block) {
        Inventory inventory = INVENTORIES.get(block.getLocation());
        if (inventory != null) return inventory;

        inventory = Bukkit.createInventory(null, plugin.getConfig().getInt("chest-inventory-size"));
        int tier = getTier(block);

        INVENTORIES.put(block.getLocation(), inventory);
        return inventory;
    }

    private int getTier(Block block) {
        return 0;
    }
}
