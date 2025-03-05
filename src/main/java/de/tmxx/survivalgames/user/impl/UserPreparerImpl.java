package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.game.phase.LobbyPhase;
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
    private final Game game;
    private final ClickableItem voteItem;
    private final ClickableItem teleportItem;

    @Inject
    UserPreparerImpl(Game game, @Vote ClickableItem voteItem, @Teleport ClickableItem teleportItem) {
        this.game = game;
        this.voteItem = voteItem;
        this.teleportItem = teleportItem;
    }

    @Override
    public void prepareUserForLobby(User user) {
        Player player = user.getPlayer();
        resetPlayer(player);

        player.setGameMode(GameMode.ADVENTURE);

        if (game.secondsLeft() <= LobbyPhase.VOTING_END_SECONDS) return;
        player.getInventory().setItem(VOTE_ITEM_SLOT, voteItem.build(player.locale()));
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
        player.getInventory().setItem(TELEPORTER_ITEM_SLOT, teleportItem.build(player.locale()));
    }

    @Override
    public void prepareUserForEnding(User user) {
        user.showForAllPlayers();
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
