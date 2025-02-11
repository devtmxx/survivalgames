package de.tmxx.survivalgames.auto;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.game.GameState;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
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

    private static final Multimap<GameState, Listener> LISTENERS = HashMultimap.create();

    public static void registerListeners(SurvivalGames plugin) {
        registerPhaseListeners(plugin, GameState.NONE);
    }

    public static void registerPhaseListeners(SurvivalGames plugin, GameState state) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        getClasses(plugin, Listener.class, LISTENERS_PACKAGE, state).forEach(clazz -> registerListener(plugin, pluginManager, clazz, state));
    }

    public static void unregisterPhaseListeners(GameState state) {
        LISTENERS.removeAll(state).forEach(HandlerList::unregisterAll);
    }

    public static void registerCommands(SurvivalGames plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            getClasses(plugin, AutoCommand.class, COMMANDS_PACKAGE, GameState.NONE).forEach(clazz -> registerCommand(plugin, event.registrar(), clazz));
        });
    }

    private static void registerListener(SurvivalGames plugin, PluginManager pluginManager, Class<? extends Listener> listenerClass, GameState state) {
        try {
            Listener listener = null;
            for (Constructor<?> constructor : listenerClass.getDeclaredConstructors()) {
                if (constructor.getParameterCount() == 0) {
                    listener = (Listener) constructor.newInstance();
                    break;
                }
                if (constructor.getParameterCount() == 1 && constructor.getParameterTypes()[0].isAssignableFrom(plugin.getClass())) {
                    listener = (Listener) constructor.newInstance(plugin);
                }
            }
            if (listener == null) return;

            LISTENERS.put(state, listener);
            pluginManager.registerEvents(listener, plugin);
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

    private static <T> List<Class<? extends T>> getClasses(SurvivalGames plugin, Class<T> baseClass, String packageName, GameState state) {
        boolean isSetup = plugin.isSetup();
        List<Class<? extends T>> list = new ArrayList<>();

        try {
            ClassPath.from(plugin.getClass().getClassLoader()).getTopLevelClassesRecursive(packageName).forEach(classInfo -> {
                Class<?> clazz = classInfo.load();
                if (!baseClass.isAssignableFrom(clazz)) return;

                Class<? extends T> subclass = clazz.asSubclass(baseClass);
                if (!subclass.isAnnotationPresent(AutoRegister.class)) return;

                AutoRegister register = subclass.getAnnotation(AutoRegister.class);
                RegisterState regState = register.value();
                if (!regState.equals(RegisterState.ALWAYS) &&
                        !(regState.equals(RegisterState.SETUP) && isSetup) &&
                        !(regState.equals(RegisterState.GAME) && !isSetup)) return;

                if (compareStates(register.states(), state))
                    list.add(subclass);
            });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while reading class info", e);
        }

        return list;
    }

    private static boolean compareStates(GameState[] states, GameState state) {
        for (GameState toCheck : states) {
            if (state.equals(toCheck)) return true;
        }
        return false;
    }
}
