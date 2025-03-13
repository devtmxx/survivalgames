package de.tmxx.survivalgames.scoreboard.placeholder;

import com.google.common.reflect.ClassPath;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.module.game.PluginLogger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class PlaceholderApplier {
    private final Logger logger;
    private final Set<Placeholder> placeholders = new HashSet<>();

    @Inject
    PlaceholderApplier(@PluginLogger Logger logger) {
        this.logger = logger;

        loadPlaceholders();
    }

    public Component apply(String input, Locale locale) {
        for (Placeholder placeholder : placeholders) {
            input = input.replaceAll(placeholder.variable(), placeholder.replacement(locale));
        }
        return MiniMessage.miniMessage().deserialize(input);
    }

    private void loadPlaceholders() {
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClasses(getClass().getPackageName()).forEach(classInfo -> {
                Class<?> clazz = classInfo.load();
                if (!Placeholder.class.isAssignableFrom(clazz) || clazz.equals(Placeholder.class)) return;

                Class<? extends Placeholder> placeholderClass = clazz.asSubclass(Placeholder.class);
                placeholders.add(SurvivalGames.unsafe().getInstance(placeholderClass));
                logger.info("Loaded scoreboard placeholder " + placeholderClass.getSimpleName());
            });
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error while loading scoreboard placeholders");
        }
    }
}
