package de.tmxx.survivalgames.module.game;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.chest.ChestFiller;
import de.tmxx.survivalgames.chest.ChestFillerImpl;
import de.tmxx.survivalgames.command.CommandRegistrar;
import de.tmxx.survivalgames.command.impl.GameCommandRegistrar;
import de.tmxx.survivalgames.command.impl.SetupCommandRegistrar;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GameHandler;
import de.tmxx.survivalgames.game.impl.GameHandlerImpl;
import de.tmxx.survivalgames.game.impl.GameImpl;
import de.tmxx.survivalgames.game.phase.*;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.i18n.impl.I18nImpl;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.listener.ListenerRegistrarImpl;
import de.tmxx.survivalgames.map.*;
import de.tmxx.survivalgames.map.impl.MapImpl;
import de.tmxx.survivalgames.map.impl.MapManagerImpl;
import de.tmxx.survivalgames.module.config.Setup;
import de.tmxx.survivalgames.user.*;
import de.tmxx.survivalgames.user.impl.UserBroadcasterImpl;
import de.tmxx.survivalgames.user.impl.UserImpl;
import de.tmxx.survivalgames.user.impl.UserRegistryImpl;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@RequiredArgsConstructor
public class GameModule extends AbstractModule {
    private final SurvivalGames plugin;

    @Override
    protected void configure() {
        bind(JavaPlugin.class).toInstance(plugin);
        bind(Logger.class).annotatedWith(PluginLogger.class).toInstance(plugin.getLogger());
        bind(I18n.class).to(I18nImpl.class);
        bind(ListenerRegistrar.class).to(ListenerRegistrarImpl.class);

        install(new FactoryModuleBuilder()
                .implement(User.class, UserImpl.class)
                .build(UserFactory.class));
        install(new FactoryModuleBuilder()
                .implement(Map.class, MapImpl.class)
                .build(MapFactory.class));

        bind(MapManager.class).to(MapManagerImpl.class);
        bind(UserBroadcaster.class).to(UserBroadcasterImpl.class);
        bind(UserRegistry.class).to(UserRegistryImpl.class);
        bind(GameHandler.class).to(GameHandlerImpl.class);
        bind(Game.class).to(GameImpl.class);

        bind(GamePhase.class).annotatedWith(Lobby.class).to(LobbyPhase.class);
        bind(GamePhase.class).annotatedWith(Starting.class).to(StartingPhase.class);
        bind(GamePhase.class).annotatedWith(InGame.class).to(InGamePhase.class);
        bind(GamePhase.class).annotatedWith(DeathMatch.class).to(DeathMatchPhase.class);
        bind(GamePhase.class).annotatedWith(Ending.class).to(EndingPhase.class);

        bind(ChestFiller.class).to(ChestFillerImpl.class);
    }

    @Provides
    @Singleton
    @MapsDirectory
    File provideMapsDirectory() {
        return new File(plugin.getDataFolder(), "maps");
    }

    @Provides
    @WorldsContainer
    @Singleton
    File provideWorldsContainer() {
        return new File(plugin.getConfig().getString("worlds-container", "worlds"));
    }

    @Provides
    CommandRegistrar provideCommandRegistrar(@Setup boolean setup, SetupCommandRegistrar setupRegistrar, GameCommandRegistrar gameRegistrar) {
        return setup ? setupRegistrar : gameRegistrar;
    }
}
