package de.tmxx.survivalgames.chest;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.config.ChestItem;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.config.TiersConfig;
import de.tmxx.survivalgames.module.game.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class ChestFillerImpl implements ChestFiller {
    private static final Random RANDOM = new Random();

    private final Logger logger;
    private final FileConfiguration mainConfig;
    private final FileConfiguration tiersConfig;
    private final Map<Integer, ChestTier> tiers = new HashMap<>();
    private final Map<Location, Inventory> inventories = new HashMap<>();
    private int defaultTier = 0;

    @Inject
    public ChestFillerImpl(
            @PluginLogger Logger logger,
            @MainConfig FileConfiguration config,
            @TiersConfig FileConfiguration tiersConfig
    ) {
        this.logger = logger;
        this.mainConfig = config;
        this.tiersConfig = tiersConfig;
        load();
    }

    @Override
    public Inventory fill(Chest chest) {
        Inventory inventory = inventories.get(chest.getLocation());
        if (inventory != null) return inventory;

        inventory = Bukkit.createInventory(null, mainConfig.getInt("chest.inventory-size"));
        inventories.put(chest.getLocation(), inventory);

        ItemStack firstItem = chest.getInventory().getItem(0);
        ChestTier tier = getTier(firstItem);

        randomizeItems(inventory, tier);

        return inventory;
    }

    private ChestTier getTier(ItemStack stack) {
        if (stack == null) return tiers.get(defaultTier);

        Material material = stack.getType();
        int tier = mainConfig.getInt("tiers.%s".formatted(material.name()));
        return tiers.get(tier);
    }

    private void randomizeItems(Inventory inventory, ChestTier tier) {
        int min = mainConfig.getInt("chest.items.min");
        int max = mainConfig.getInt("chest.items.max");
        int amount = RANDOM.nextInt(min, max);

        for (int i = 0; i < amount; i++) {
            ChestItem item = tier.getRandomItem();
            if (item == null) break;

            int slot = RANDOM.nextInt(inventory.getSize());
            inventory.setItem(slot, item.build());
        }
    }

    private void load() {
        ConfigurationSection tiersSection = mainConfig.getConfigurationSection("tiers");
        if (tiersSection == null) return;

        defaultTier = tiersSection.getInt("default", 0);
        List<Integer> tiers = tiersSection.getKeys(false).stream().map(tiersSection::getInt).toList();

        for (int tier : tiers) {
            ChestTier chestTier = new ChestTier(tiersConfig.getMapList("tiers." + tier));
            this.tiers.put(tier, chestTier);
        }

        logger.info("Loaded " + this.tiers.size() + " chest tiers");
    }
}
