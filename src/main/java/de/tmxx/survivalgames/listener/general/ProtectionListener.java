package de.tmxx.survivalgames.listener.general;

import de.tmxx.survivalgames.listener.RegisterAlways;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.Set;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class ProtectionListener implements Listener {
    private static final Set<Material> LAVA_FORM_RESULTS = Set.of(
            Material.OBSIDIAN,
            Material.STONE,
            Material.COBBLESTONE
    );

    private static final Set<Material> LIQUIDS = Set.of(
            Material.LAVA,
            Material.WATER
    );

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        // prevent all block fades
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        // cancel all lava generated formations
        event.setCancelled(LAVA_FORM_RESULTS.contains(event.getNewState().getType()));
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        // cancel water and lava flow
        event.setCancelled(LIQUIDS.contains(event.getToBlock().getType()));
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        // cancel all block spreads, i.e. fire
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent event) {
        // leaves are not allowed to decay under normal circumstances at all. nevertheless players could be able to
        // break leaves depending on the configuration.
        event.setCancelled(true);
    }
}
