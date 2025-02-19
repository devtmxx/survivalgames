package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.item.ClickableItem;
import de.tmxx.survivalgames.module.game.interactable.Teleport;
import de.tmxx.survivalgames.module.game.interactable.Vote;
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
    private static final int FIRST_HOTBAR_SLOT = 0;

    private final ClickableItem voteItem;
    private final ClickableItem teleportItem;

    @Inject
    UserPreparerImpl(@Vote ClickableItem voteItem, @Teleport ClickableItem teleportItem) {
        this.voteItem = voteItem;
        this.teleportItem = teleportItem;
    }

    @Override
    public void prepareUserForLobby(User user) {
        Player player = user.getPlayer();
        resetPlayer(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().setItem(FIRST_HOTBAR_SLOT, voteItem.build(player.locale()));
    }

    @Override
    public void prepareUserForGame(User user) {
        Player player = user.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
    }

    @Override
    public void prepareUserForSpectator(User user) {
        Player player = user.getPlayer();
        resetPlayer(player);

        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setItem(FIRST_HOTBAR_SLOT, teleportItem.build(player.locale()));
    }

    private void resetPlayer(Player player) {
        player.setHealth(20.0D);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        player.clearActivePotionEffects();
        player.getInventory().clear();
        player.updateInventory();
    }
}
