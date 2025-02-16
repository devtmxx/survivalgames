package de.tmxx.survivalgames.i18n;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.*;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface I18n {
    Locale FALLBACK_LOCALE = Locale.US;
    String TRANSLATION_NOT_FOUND = "N/A";

    Component translate(Locale locale, String key, Object... args);
    Component translate(MiniMessage miniMessage, Locale locale, String key, Object... args);
    List<Component> translateList(Locale locale, String key, Object... args);
    List<Component> translateList(MiniMessage miniMessage, Locale locale, String key, Object... args);
    String translateRaw(String key, Object... args);
    String translateRaw(Locale locale, String key, Object... args);
}
