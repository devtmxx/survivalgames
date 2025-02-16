package de.tmxx.survivalgames.command;

import com.google.common.reflect.ClassPath;
import com.google.inject.Injector;
import de.tmxx.survivalgames.SurvivalGames;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface CommandRegistrar {
    void register();

    default List<Class<? extends Command>> loadClasses(Class<? extends Annotation> annotation, Logger logger) {
        List<Class<? extends Command>> classes = new ArrayList<>();

        String packageName = CommandRegistrar.class.getPackage().getName();
        try {
            ClassPath.from(getClass().getClassLoader()).getTopLevelClassesRecursive(packageName).forEach(classInfo -> {
                Class<?> clazz = classInfo.load();
                if (!Command.class.isAssignableFrom(clazz) || clazz.equals(Command.class)) return;

                Class<? extends Command> command = clazz.asSubclass(Command.class);
                if (!command.isAnnotationPresent(annotation)) return;

                classes.add(command);
            });
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load command classes", e);
        }

        return classes;
    }

    default void register(List<Class<? extends Command>> commands, JavaPlugin plugin) {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        Injector injector = SurvivalGames.unsafe();

        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            commands.forEach(clazz -> {
                Command command = injector.getInstance(clazz);
                event.registrar().register(command.name(), command.aliases(), command);
            });
        });
    }
}
