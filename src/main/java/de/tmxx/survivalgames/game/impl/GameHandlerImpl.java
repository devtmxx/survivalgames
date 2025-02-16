package de.tmxx.survivalgames.game.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.command.CommandRegistrar;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.GameHandler;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.map.MapManager;

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

    @Inject
    public GameHandlerImpl(MapManager mapManager, ListenerRegistrar listenerRegistrar, CommandRegistrar commandRegistrar, Game game) {
        this.mapManager = mapManager;
        this.listenerRegistrar = listenerRegistrar;
        this.commandRegistrar = commandRegistrar;
        this.game = game;
    }

    @Override
    public void startup() {
        listenerRegistrar.registerGeneral();
        commandRegistrar.register();

        mapManager.load();

        game.startGame();
    }

    @Override
    public void shutdown() {
        game.stopGame();
    }
}
