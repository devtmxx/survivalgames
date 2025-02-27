package de.tmxx.survivalgames.user;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public interface UserPreparer {
    void prepareUserForLobby(User user);
    void prepareUserForGame(User user);
    void prepareUserForSpectator(User user);
    void prepareUserForEnding(User user);
}
