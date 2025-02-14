package de.tmxx.survivalgames.chest;

import de.tmxx.survivalgames.SurvivalGames;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class ChestFiller {
    private final Map<Integer, ChestTier> tiers = new HashMap<>();
    private int defaultTier = 0;

    public ChestFiller(SurvivalGames plugin) {
        plugin.saveResource("tiers.yml", false);

        ConfigurationSection tiersSection = plugin.getConfig().getConfigurationSection("tiers");
        if (tiersSection == null) return;

        defaultTier = tiersSection.getInt("default", 0);
        List<Integer> tiers = tiersSection.getKeys(false).stream().map(tiersSection::getInt).toList();

        FileConfiguration tiersConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "tiers.yml"));
        for (int tier : tiers) {
            ChestTier chestTier = new ChestTier(tiersConfig.getMapList("tiers." + tier));
            this.tiers.put(tier, chestTier);
        }

        plugin.getLogger().info("Loaded " + this.tiers.size() + " chest tiers");
    }

    public ChestTier getTier(int tier) {
        if (tiers.containsKey(tier)) return tiers.get(tier);
        return tiers.get(defaultTier);
    }
}
