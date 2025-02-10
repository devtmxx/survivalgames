package de.tmxx.survivalgames.i18n;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class I18nCache {
    private static final String REPLACE = "%prefix%";

    private final Map<String, String> messages;

    public String get(String key, Object... args) {
        if (!messages.containsKey(key)) return I18n.TRANSLATION_NOT_FOUND;
        return MessageFormat.format(messages.get(key), args);
    }

    public static @NotNull I18nCache create(@NotNull FileConfiguration config) {
        String prefix = config.getString("prefix");
        if (prefix != null) return new I18nCache(replacePrefix(config, prefix));

        Map<String, String> messages = new HashMap<>();
        config.getKeys(true).forEach(key -> messages.put(key, config.getString(key)));
        return new I18nCache(messages);
    }

    private static @NotNull Map<String, String> replacePrefix(@NotNull FileConfiguration config, @NotNull String prefix) {
        Map<String, String> messages = new HashMap<>();
        config.getKeys(true).forEach(key -> messages.put(key, Objects.requireNonNull(config.getString(key)).replaceAll(REPLACE, prefix)));
        return messages;
    }
}
