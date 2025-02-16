package de.tmxx.survivalgames.map;

import java.io.File;

/**
 * Project: survivalgames
 * 15.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface MapFactory {
    Map create(File configFile);
}
