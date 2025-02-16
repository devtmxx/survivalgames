package de.tmxx.survivalgames.listener;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.module.game.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class ListenerRegistrarImpl implements ListenerRegistrar {
    private final Logger logger;
    private final JavaPlugin plugin;
    private final PluginManager pluginManager;

    private boolean loaded = false;
    private final Multimap<Class<? extends Annotation>, Class<? extends Listener>> listenerClasses = HashMultimap.create();
    private final Multimap<Class<? extends Annotation>, Listener> registeredListeners = HashMultimap.create();

    public ListenerRegistrarImpl(@PluginLogger Logger logger, JavaPlugin plugin) {
        this.logger = logger;
        this.plugin = plugin;
        pluginManager = Bukkit.getPluginManager();
    }

    @Override
    public void registerGeneral() {
        registerPhaseSpecific(RegisterAlways.class);
    }

    @Override
    public void registerPhaseSpecific(@NotNull Class<? extends Annotation> annotation) {
        loadClassesIfNotLoaded();

        Injector injector = SurvivalGames.unsafe();
        listenerClasses.get(annotation).forEach(clazz -> {
            Listener listener = injector.getInstance(clazz);

            pluginManager.registerEvents(listener, plugin);
            registeredListeners.put(annotation, listener);
        });
    }

    @Override
    public void unregisterPhaseSpecific(Class<? extends Annotation> annotation) {

    }

    private void loadClassesIfNotLoaded() {
        if (loaded) return;
        loaded = true;

        String packageName = getClass().getPackage().getName();
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(packageName).forEach(classInfo -> {
                Class<?> clazz = classInfo.load();
                if (!Listener.class.isAssignableFrom(clazz)) return;

                Class<? extends Listener> listenerClass = clazz.asSubclass(Listener.class);
                for (Annotation annotation : clazz.getAnnotations()) {
                    listenerClasses.put(annotation.getClass(), listenerClass);
                }
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while loading classes from package: " + packageName, e);
        }
    }
}
