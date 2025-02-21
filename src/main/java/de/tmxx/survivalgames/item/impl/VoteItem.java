package de.tmxx.survivalgames.item.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.inventory.InventoryGUI;
import de.tmxx.survivalgames.item.ClickableItem;
import de.tmxx.survivalgames.item.ItemRegistry;
import de.tmxx.survivalgames.module.game.interactable.Vote;
import de.tmxx.survivalgames.user.User;
import net.kyori.adventure.text.format.TextDecoration;
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
public class VoteItem implements ClickableItem {
    private final I18n i18n;
    private final InventoryGUI inventory;

    @Inject
    VoteItem(I18n i18n, ItemRegistry registry, @Vote InventoryGUI inventory) {
        this.i18n = i18n;
        this.inventory = inventory;
        registry.registerItem(this);
    }

    @Override
    public String getId() {
        return "vote";
    }

    @Override
    public void onClick(User user) {
        inventory.openInventory(user);
    }

    @Override
    public ItemStack build(Locale locale) {
        ItemStack item = ItemStack.of(Material.MAP);
        item.editMeta(meta -> {
            meta.displayName(i18n.translate(locale, "item.vote.name").decoration(TextDecoration.ITALIC, false));
            meta.lore(i18n.translateList(locale, "item.vote.lore"));
        });
        setPersistentData(item);
        return item;
    }
}
