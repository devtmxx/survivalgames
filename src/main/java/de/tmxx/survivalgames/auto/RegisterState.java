package de.tmxx.survivalgames.auto;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * <p>
 *     Used to tell when an auto-registrable entity should be registered.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public enum RegisterState {
    /**
     * Should be registered during setup.
     */
    SETUP,

    /**
     * Should be registered during the game.
     */
    GAME,

    /**
     * Should always be registered
     */
    ALWAYS
}
