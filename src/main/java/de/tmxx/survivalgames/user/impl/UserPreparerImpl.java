package de.tmxx.survivalgames.user.impl;

import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserPreparer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Project: survivalgames
 * 16.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
public class UserPreparerImpl implements UserPreparer {
    @Override
    public void prepareUserForLobby(User user) {
        Player player = user.getPlayer();
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.updateInventory();
        player.setGameMode(GameMode.ADVENTURE);
    }

    @Override
    public void prepareUserForGame(User user) {
        Player player = user.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void prepareUserForSpectator(User user) {

    }
}
