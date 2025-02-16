package de.tmxx.survivalgames;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.tmxx.survivalgames.config.SpawnPosition;
import de.tmxx.survivalgames.game.GameHandler;
import de.tmxx.survivalgames.module.config.ConfigModule;
import de.tmxx.survivalgames.module.game.GameModule;
import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Getter
public class SurvivalGames extends JavaPlugin {
    public static final int CONFIG_VERSION = 1;

    private static Injector injector;
    private GameHandler gameHandler;

    @Override
    public void onEnable() {
        registerConfigurationSerializables();

        injector = Guice.createInjector(
                new ConfigModule(this),
                new GameModule(this)
        );

        gameHandler = injector.getInstance(GameHandler.class);
        gameHandler.startup();
    }

    @Override
    public void onDisable() {
        gameHandler.shutdown();
    }

    private void registerConfigurationSerializables() {
        ConfigurationSerialization.registerClass(SpawnPosition.class);
    }

    public static Injector unsafe() {
        return injector;
    }
}
