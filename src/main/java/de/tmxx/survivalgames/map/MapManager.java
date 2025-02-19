package de.tmxx.survivalgames.map;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface MapManager {
    void load();
    @Nullable Map create(String id);
    @Nullable Map get(String id);
    Collection<Map> getUsableMaps();
    Collection<Map> getAll();
    void endVoting();
    boolean hasVotingEnded();
    Map getVotedMap();
}
