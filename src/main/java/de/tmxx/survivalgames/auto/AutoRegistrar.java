package de.tmxx.survivalgames.auto;

import com.google.common.reflect.ClassPath;
import de.tmxx.survivalgames.SurvivalGames;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class AutoRegistrar {
    private static final String LISTENERS_PACKAGE = "de.tmxx.survivalgames.listener";
    private static final String COMMANDS_PACKAGE = "de.tmxx.survivalgames.command";

    public static void registerListeners(SurvivalGames plugin) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        getClasses(plugin, Listener.class, LISTENERS_PACKAGE).forEach(clazz -> registerListener(plugin, pluginManager, clazz));
    }

    public static void registerCommands(SurvivalGames plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            getClasses(plugin, AutoCommand.class, COMMANDS_PACKAGE).forEach(clazz -> registerCommand(plugin, event.registrar(), clazz));
        });
    }

    private static void registerListener(SurvivalGames plugin, PluginManager pluginManager, Class<? extends Listener> listenerClass) {
        try {
            Constructor<? extends Listener> constructor = listenerClass.getDeclaredConstructor(SurvivalGames.class);
            Listener listener = constructor.newInstance(plugin);
            pluginManager.registerEvents(listener, plugin);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("Could not register " + listenerClass.getSimpleName() + " because there is no suitable constructor");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while registering listener", e);
        }
    }

    private static void registerCommand(SurvivalGames plugin, Commands commands, Class<? extends AutoCommand> commandClass) {
        try {
            Constructor<? extends AutoCommand> constructor = commandClass.getDeclaredConstructor(SurvivalGames.class);
            AutoCommand command = constructor.newInstance(plugin);
            commands.register(command.name(), command.aliases(), command);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("Could not register " + commandClass.getSimpleName() + " because there is no suitable constructor");
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while registering command", e);
        }
    }

    private static <T> List<Class<? extends T>> getClasses(SurvivalGames plugin, Class<T> baseClass, String packageName) {
        boolean isSetup = plugin.isSetup();
        List<Class<? extends T>> list = new ArrayList<>();

        try {
            ClassPath.from(plugin.getClass().getClassLoader()).getTopLevelClassesRecursive(packageName).forEach(classInfo -> {
                Class<?> clazz = classInfo.load();
                if (!baseClass.isAssignableFrom(clazz)) return;

                Class<? extends T> subclass = clazz.asSubclass(baseClass);
                if (!subclass.isAnnotationPresent(AutoRegister.class)) return;

                RegisterState state = subclass.getAnnotation(AutoRegister.class).value();
                if (state.equals(RegisterState.ALWAYS) ||
                        (state.equals(RegisterState.SETUP) && isSetup) ||
                        (state.equals(RegisterState.GAME) && !isSetup)) {
                    list.add(subclass);
                }
            });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while reading class info", e);
        }

        return list;
    }
}
