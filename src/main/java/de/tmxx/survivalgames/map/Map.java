package de.tmxx.survivalgames.map;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface Map {
    String getId();
    void loadWorld();
    boolean isUsable();
    @Nullable Location getSpawnPosition(int index);
    boolean addSpawnPosition(Location location);
    int amountOfSpawns();
    boolean setSpectatorSpawn(Location location);
    void setName(String name);
    String getName();
    void setWorld(String world);
    void setAuthor(String author);
    String getAuthor();
    void save();
    void castVote(UUID uniqueId);
    boolean hasVoted(UUID uniqueId);
    int getVotes();
}
