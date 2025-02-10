package de.tmxx.survivalgames;

import de.tmxx.survivalgames.i18n.I18n;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class SurvivalGames extends JavaPlugin {
    private static final int CONFIG_VERSION = 1;

    @Getter private I18n i18n;

    @Override
    public void onEnable() {
        loadConfig();
        i18n = new I18n(this);
    }

    private void loadConfig() {
        saveDefaultConfig();
        if (getConfig().getInt("version") >= CONFIG_VERSION) return;

        updateConfig();
    }

    private void updateConfig() {
        FileConfiguration config = getConfig();

        saveResource("config.yml", true);
        reloadConfig();

        FileConfiguration newConfig = getConfig();
        config.getKeys(true).forEach(key -> newConfig.set(key, config.get(key)));

        saveConfig();
    }
}
