package de.tmxx.survivalgames.config;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class ChestItem {
    private final Material material;
    private final int min;
    private final int max;
    @Getter private final int weight;

    public ChestItem(Map<?, ?> data) {
        material = Material.valueOf((String) data.get("material"));
        min = (int) data.get("min");
        max = (int) data.get("max");
        weight = (int) data.get("weight");
    }

    public ItemStack build() {
        int amount = (int) (min + Math.random() * (max - min));
        return ItemStack.of(material).asQuantity(amount);
    }
}
