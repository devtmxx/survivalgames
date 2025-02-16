package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.chest.ChestFiller;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@InGame
@DeathMatch
public class ChestListener implements Listener {
    private final ChestFiller filler;

    @Inject
    public ChestListener(ChestFiller filler) {
        this.filler = filler;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false)
    public void onChestOpen(PlayerInteractEvent event) {
        // ignore all interactions where players do not right-click a block
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        // the clicked block will never be null since the action suggests, that a block has been clicked, but this
        // if-clause will prevent IntelliJ from warning about a possible NPE
        if (event.getClickedBlock() == null) return;
        if (!(event.getClickedBlock().getState() instanceof Chest chest)) return;
        if (chest.isBlocked()) return;

        Inventory inventory = filler.fill(chest);
        event.getPlayer().openInventory(inventory);
    }
}
