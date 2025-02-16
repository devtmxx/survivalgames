package de.tmxx.survivalgames.map;

import com.google.inject.Singleton;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Singleton
public interface MapManager {
    void load();
    @Nullable Map create(String id);
    @Nullable Map get(String id);
    Collection<Map> getUsableMaps();
    Collection<Map> getAll();
}
