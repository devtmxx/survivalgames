package de.tmxx.survivalgames.i18n.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.i18n.I18nCache;
import de.tmxx.survivalgames.module.config.DefaultLocale;
import de.tmxx.survivalgames.module.config.LocalesDirectory;
import de.tmxx.survivalgames.module.config.SupportedLocales;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class I18nImpl implements I18n {
    private static final String MESSAGE_SPLIT = "%n";
    private static final Set<Locale> DEFAULT_LOCALES = Set.of(
            Locale.US,
            Locale.GERMANY
    );

    private final File directory;
    private final Locale defaultLocale;
    private final List<Locale> supportedLocales;
    private final Map<Locale, I18nCache> cache = new HashMap<>();

    @Inject
    I18nImpl(
            JavaPlugin plugin,
            @LocalesDirectory File directory,
            @DefaultLocale Locale defaultLocale,
            @SupportedLocales List<Locale> supportedLocales
    ) {
        this.directory = directory;
        this.defaultLocale = defaultLocale;
        this.supportedLocales = supportedLocales;

        saveDefaultLocales(plugin);
        loadCache();
    }

    public Component translate(Locale locale, String key, Object... args) {
        return translate(MiniMessage.miniMessage(), locale, key, args);
    }

    public Component translate(MiniMessage miniMessage, Locale locale, String key, Object... args) {
        return miniMessage.deserialize(translateRaw(locale, key, args));
    }

    public List<Component> translateList(Locale locale, String key, Object... args) {
        return translateList(MiniMessage.miniMessage(), locale, key, args);
    }

    public List<Component> translateList(MiniMessage miniMessage, Locale locale, String key, Object... args) {
        return Arrays.stream(translateRaw(locale, key, args).split(MESSAGE_SPLIT)).map(miniMessage::deserialize).toList();
    }

    public String translateRaw(String key, Object... args) {
        return translateRaw(defaultLocale, key, args);
    }

    public String translateRaw(Locale locale, String key, Object... args) {
        if (!locale.equals(defaultLocale) && !supportedLocales.contains(locale)) return translateRaw(defaultLocale, key, args);

        I18nCache cache = this.cache.get(locale);
        if (cache == null) return TRANSLATION_NOT_FOUND;

        return cache.get(key, args);
    }

    private void loadCache() {
        supportedLocales.forEach(this::loadCache);
    }

    private void loadCache(Locale locale) {
        File localeFile = new File(directory, locale.toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
        cache.put(locale, I18nCache.create(config));
    }

    private void saveDefaultLocales(Plugin plugin) {
        DEFAULT_LOCALES.forEach(locale -> {
            File file = new File(directory, locale.toString() + ".yml");
            // Skipping saveResource if the file exists because this will otherwise print an ugly warning into the console
            if (file.exists()) return;

            plugin.saveResource("locales/%s.yml".formatted(locale.toString()), false);
        });
    }
}
