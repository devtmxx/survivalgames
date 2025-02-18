package de.tmxx.survivalgames.item.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.inventory.InventoryGUI;
import de.tmxx.survivalgames.item.ClickableItem;
import de.tmxx.survivalgames.item.ItemRegistry;
import de.tmxx.survivalgames.module.game.interactable.Teleport;
import de.tmxx.survivalgames.user.User;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class TeleportItem implements ClickableItem {
    private final I18n i18n;
    private final InventoryGUI inventory;

    @Inject
    TeleportItem(I18n i18n, ItemRegistry registry, @Teleport InventoryGUI inventory) {
        this.i18n = i18n;
        this.inventory = inventory;
        registry.registerItem(this);
    }

    @Override
    public String getId() {
        return "teleport";
    }

    @Override
    public void onClick(User user) {
        inventory.openInventory(user);
    }

    @Override
    public ItemStack build(Locale locale) {
        ItemStack item = ItemStack.of(Material.PLAYER_HEAD);
        item.editMeta(meta -> {
            meta.displayName(i18n.translate(locale, "item.teleport.name"));
            meta.lore(i18n.translateList(locale, "item.teleport.lore"));
        });
        setPersistentData(item);
        return item;
    }
}
