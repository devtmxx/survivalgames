package de.tmxx.survivalgames.game.phase;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.tmxx.survivalgames.listener.ListenerRegistrar;
import de.tmxx.survivalgames.module.game.phase.InGame;
import org.bukkit.Location;

/**
 * Project: survivalgames
 * 16.02.25
 * 
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public class InGamePhase implements GamePhase {
    private final ListenerRegistrar listenerRegistrar;

    @Inject
    InGamePhase(ListenerRegistrar listenerRegistrar) {
        this.listenerRegistrar = listenerRegistrar;
    }

    @Override
    public void start() {
        listenerRegistrar.registerPhaseSpecific(InGame.class);
    }

    @Override
    public void tick() {

    }

    @Override
    public void end() {
        listenerRegistrar.unregisterPhaseSpecific(InGame.class);
    }

    @Override
    public int countdownSeconds() {
        return 1;
    }

    @Override
    public Location spawnLocation() {
        return null;
    }

    @Override
    public void nextPhase() {

    }
}
