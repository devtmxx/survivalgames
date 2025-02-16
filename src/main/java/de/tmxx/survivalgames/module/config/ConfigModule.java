package de.tmxx.survivalgames.module.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.i18n.I18n;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;
import java.util.Locale;

import static de.tmxx.survivalgames.SurvivalGames.CONFIG_VERSION;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@RequiredArgsConstructor
public class ConfigModule extends AbstractModule {
    private final SurvivalGames plugin;

    @Override
    protected void configure() {

    }

    @Provides
    @MainConfig
    @Singleton
    FileConfiguration provideFileConfiguration() {
        createDefaultConfigFileIfNotExists("config.yml");
        updateConfigIfVersionIsOutdated();
        return plugin.getConfig();
    }

    @Provides
    @TiersConfig
    @Singleton
    FileConfiguration provideTiersConfiguration() {
        createDefaultConfigFileIfNotExists("tiers.yml");
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "tiers.yml"));
    }

    @Provides
    @MinPlayers
    @Singleton
    int provideMinPlayers() {
        return Math.max(plugin.getConfig().getInt("players.min"), 1);
    }

    @Provides
    @MaxPlayers
    @Singleton
    int provideMaxPlayers(@MinPlayers int minPlayers) {
        return Math.max(plugin.getConfig().getInt("players.max"), minPlayers);
    }

    @Provides
    @Setup
    @Singleton
    boolean provideSetup() {
        return plugin.getConfig().getBoolean("setup");
    }

    @Provides
    @LocalesDirectory
    @Singleton
    File provideLocalesDirectory() {
        String directoryPath = plugin.getConfig().getString("locales.directory", "locales");
        return directoryPath.startsWith("/") ? new File(directoryPath) : new File(plugin.getDataFolder(), directoryPath);
    }

    @Provides
    @DefaultLocale
    @Singleton
    Locale provideDefaultLocale() {
        return localeFromString(plugin.getConfig().getString("locales.default"));
    }

    @Provides
    @SupportedLocales
    @Singleton
    List<Locale> provideSupportedLocales() {
        return plugin.getConfig().getStringList("locales.supported").stream().map(this::localeFromString).toList();
    }

    private void createDefaultConfigFileIfNotExists(String name) {
        File configFile = new File(plugin.getDataFolder(), name);
        if (configFile.exists()) return;
        plugin.saveResource(name, false);
    }

    private void updateConfigIfVersionIsOutdated() {
        if (plugin.getConfig().getInt("version") >= CONFIG_VERSION) return;

        FileConfiguration config = plugin.getConfig();

        plugin.saveResource("config.yml", true);
        plugin.reloadConfig();

        FileConfiguration newConfig = plugin.getConfig();
        config.getKeys(true).forEach(key -> newConfig.set(key, config.get(key)));
        newConfig.set("version", CONFIG_VERSION);

        plugin.saveConfig();
    }

    private Locale localeFromString(@Nullable String value) {
        if (value == null) return I18n.FALLBACK_LOCALE;

        String[] split = value.split("_");
        return split.length == 1 ?
                Locale.of(split[0]) : split.length == 2 ?
                Locale.of(split[0], split[1]) : Locale.of(split[0], split[1], split[2]);
    }
}
