package de.tmxx.survivalgames.stats.database;

import java.util.UUID;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserIDLoader {
    int loadOrCreateUserId(UUID uniqueId, String name);
    int loadUserId(String name);
}
