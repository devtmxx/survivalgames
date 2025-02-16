package de.tmxx.survivalgames.item;

import de.tmxx.survivalgames.user.User;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface ClickableItem {
    NamespacedKey KEY = new NamespacedKey("survivalgames", "item");

    String getId();
    void onClick(User user);
    ItemStack build(Locale locale);

    default void setPersistentData(ItemStack itemStack) {
        itemStack.editMeta(meta -> meta.getPersistentDataContainer().set(KEY, PersistentDataType.STRING, getId()));
    }
}
