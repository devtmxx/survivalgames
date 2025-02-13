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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
 * <p>
 *     A utility class used to automatically register commands from within the {@link #COMMANDS_PACKAGE} package or
 *     listeners from within the {@link #LISTENERS_PACKAGE} package.
 * </p>
 *
 * For further info of how to use auto registering commands and listeners see {@link AutoCommand} and
 * {@link AutoRegister}.
 *
 * @author timmauersberger
 * @version 1.0
 */
public class AutoRegistrar {
    // the packages used to auto-discover listeners and commands
    private static final String LISTENERS_PACKAGE = "de.tmxx.survivalgames.listener";
    private static final String COMMANDS_PACKAGE = "de.tmxx.survivalgames.command";

    private static final Multimap<GameState, Listener> LISTENERS = HashMultimap.create();

    /**
     * Registers all listeners from within the {@link #LISTENERS_PACKAGE} package where the {@link RegisterState} is set
     * to {@link RegisterState#ALWAYS} or {@link RegisterState#SETUP} if that scenario applies. This will ignore all
     * {@link RegisterState#GAME} states.
     * This is analogous to calling {@link #registerPhaseListeners(SurvivalGames, GameState)} with null as the game state.
     *
     * @param plugin the plugin to register the listeners for
     */
    public static void registerListeners(SurvivalGames plugin) {
        registerPhaseListeners(plugin, null);
    }

    /**
     * Registers all listeners from within the {@link #LISTENERS_PACKAGE} package that apply to the specified
     * {@link GameState}. See {@link #registerListeners(SurvivalGames)} for further information.
     *
     * @param plugin the plugin to register the listeners for
     * @param state the game state
     */
    public static void registerPhaseListeners(SurvivalGames plugin, @Nullable GameState state) {
        PluginManager pluginManager = Bukkit.getPluginManager();
        getClasses(plugin, Listener.class, LISTENERS_PACKAGE, state).forEach(clazz -> registerListener(plugin, pluginManager, clazz, state));
    }

    /**
     * Unregisters all listeners that were previously registered for a specified {@link GameState}.
     *
     * @param state the game state
     */
    public static void unregisterPhaseListeners(@NotNull GameState state) {
        LISTENERS.removeAll(state).forEach(HandlerList::unregisterAll);
    }

    /**
     * Registers all commands from within the {@link #COMMANDS_PACKAGE} package. Auto-registering commands ignore game
     * states.
     *
     * @param plugin the plugin to register the commands for
     */
    public static void registerCommands(SurvivalGames plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            getClasses(plugin, AutoCommand.class, COMMANDS_PACKAGE, null).forEach(clazz -> registerCommand(plugin, event.registrar(), clazz));
        });
    }

    /**
     * Registers a listener from the specified listener class for a game state (if specified). To work properly the
     * listener should have either a constructor with no arguments or a constructor accepting a {@link SurvivalGames}
     * instance as an argument.
     *
     * @param plugin the plugin to register the listener for
     * @param pluginManager the plugin manager to use
     * @param listenerClass the listener class
     * @param state the game state or null
     */
    private static void registerListener(
            SurvivalGames plugin,
            PluginManager pluginManager,
            Class<? extends Listener> listenerClass,
            @Nullable GameState state) {
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

            if (state != null) LISTENERS.put(state, listener);
            pluginManager.registerEvents(listener, plugin);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while registering listener", e);
        }
    }

    /**
     * Registers a command from the specified command class. To work properly the command should have a constructor
     * accepting a {@link SurvivalGames} instance as an argument.
     *
     * @param plugin the plugin to register the command for
     * @param commands the commands instance supplied by the {@link LifecycleEventManager}
     * @param commandClass the command class
     */
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

    /**
     * Scans the package for classes suitable for auto registration.
     *
     * @param plugin the plugin to register for
     * @param baseClass {@link Listener} or {@link AutoCommand}
     * @param packageName the package to scan
     * @param state the game state or null
     * @return a list of found classes
     * @param <T> the type of classes to search
     */
    private static <T> List<Class<? extends T>> getClasses(
            SurvivalGames plugin,
            Class<T> baseClass,
            String packageName,
            @Nullable GameState state) {
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

                if (regState.equals(RegisterState.ALWAYS) && state == null) {
                    // should always be registered
                    list.add(subclass);
                    return;
                }

                if (regState.equals(RegisterState.SETUP) && isSetup) {
                    // always registered during setup, as there are no game states
                    list.add(subclass);
                    return;
                }

                // do not register game entities during setup
                if (regState.equals(RegisterState.GAME) && isSetup) return;

                // register all entities assigned to the specific game state
                if (compareStates(register.states(), state)) list.add(subclass);
            });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Error while reading class info", e);
        }

        return list;
    }

    /**
     * Compares if the specified state applies to the supplied game states.
     *
     * @param states the states
     * @param state the state to check
     * @return whether the state applies to the game states
     */
    private static boolean compareStates(GameState[] states, GameState state) {
        if (state == null) return true;

        for (GameState toCheck : states) {
            if (state.equals(toCheck)) return true;
        }
        return false;
    }
}
