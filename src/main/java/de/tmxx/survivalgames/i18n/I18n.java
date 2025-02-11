package de.tmxx.survivalgames.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class I18n {
    protected static final String TRANSLATION_NOT_FOUND = "N/A";
    private static final String MESSAGE_SPLIT = "%n";
    private static final Locale FALLBACK_LOCALE = Locale.US;
    private static final Set<Locale> DEFAULT_LOCALES = Set.of(
            Locale.US,
            Locale.GERMANY
    );

    private final File directory;
    private final Locale defaultLocale;
    private final Set<Locale> supportedLocales;
    private final Map<Locale, I18nCache> cache = new HashMap<>();

    public I18n(Plugin plugin) {
        ConfigurationSection configSection = plugin.getConfig().getConfigurationSection("locales");
        if (configSection == null) throw new IllegalStateException("no locales section. config file may be corrupted");

        String directoryPath = configSection.getString("directory", "locales");
        directory = directoryPath.startsWith("/") ? new File(directoryPath) : new File(plugin.getDataFolder(), directoryPath);
        saveDefaultLocales(plugin);

        defaultLocale = localeFromString(configSection.getString("default"));
        supportedLocales = configSection.getStringList("supported").stream().map(this::localeFromString).collect(Collectors.toSet());

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

    private Locale localeFromString(@Nullable String value) {
        if (value == null) return FALLBACK_LOCALE;

        String[] split = value.split("_");
        return split.length == 1 ?
                Locale.of(split[0]) : split.length == 2 ?
                Locale.of(split[0], split[1]) : Locale.of(split[0], split[1], split[2]);
    }

    private void saveDefaultLocales(Plugin plugin) {
        DEFAULT_LOCALES.forEach(locale -> plugin.saveResource("locales/%s.yml".formatted(locale.toString()), false));
    }
}
