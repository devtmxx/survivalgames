package de.tmxx.survivalgames.listener.general;

import com.google.inject.Inject;
import de.tmxx.survivalgames.item.ClickableItem;
import de.tmxx.survivalgames.item.ItemRegistry;
import de.tmxx.survivalgames.listener.RegisterAlways;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@RegisterAlways
public class ClickableItemListener implements Listener {
    private final UserRegistry userRegistry;
    private final ItemRegistry itemRegistry;

    @Inject
    public ClickableItemListener(UserRegistry userRegistry, ItemRegistry itemRegistry) {
        this.userRegistry = userRegistry;
        this.itemRegistry = itemRegistry;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        User user = userRegistry.getUser(event.getPlayer());
        if (user == null) return;

        if (!event.getAction().isRightClick()) return;
        if (event.getItem() == null) return;

        String id = event.getItem().getPersistentDataContainer().get(ClickableItem.KEY, PersistentDataType.STRING);
        if (id == null) return;

        ClickableItem item = itemRegistry.getItem(id);
        if (item == null) return;

        item.onClick(user);
    }
}
