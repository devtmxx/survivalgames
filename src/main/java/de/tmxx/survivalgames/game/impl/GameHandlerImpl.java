package de.tmxx.survivalgames.game.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.command.CommandRegistrar;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GameHandler;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.module.config.Setup;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.stats.StatsService;
import de.tmxx.survivalgames.stats.database.Database;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class GameHandlerImpl implements GameHandler {
    private final MapManager mapManager;
    private final ListenerRegistrar listenerRegistrar;
    private final CommandRegistrar commandRegistrar;
    private final Game game;
    private final GamePhaseChanger gamePhaseChanger;
    private final GamePhase lobbyPhase;
    private final boolean setup;
    private final Database database;
    private final StatsService statsService;
    private final UserRegistry userRegistry;

    @Inject
    GameHandlerImpl(
            MapManager mapManager,
            ListenerRegistrar listenerRegistrar,
            CommandRegistrar commandRegistrar,
            Game game,
            GamePhaseChanger gamePhaseChanger,
            @Lobby GamePhase lobbyPhase,
            @Setup boolean setup,
            Database database,
            StatsService statsService,
            UserRegistry userRegistry
    ) {
        this.mapManager = mapManager;
        this.listenerRegistrar = listenerRegistrar;
        this.commandRegistrar = commandRegistrar;
        this.game = game;
        this.gamePhaseChanger = gamePhaseChanger;
        this.lobbyPhase = lobbyPhase;
        this.setup = setup;
        this.database = database;
        this.statsService = statsService;
        this.userRegistry = userRegistry;
    }

    @Override
    public void startup() {
        listenerRegistrar.registerGeneral();
        commandRegistrar.register();

        if (setup) return;

        database.connect();
        statsService.prepare();

        mapManager.load();

        gamePhaseChanger.changeGamePhase(lobbyPhase);
        game.startGame();
    }

    @Override
    public void shutdown() {
        userRegistry.getOnlineUsers().forEach(User::saveStats);
        game.stopGame();
    }
}
