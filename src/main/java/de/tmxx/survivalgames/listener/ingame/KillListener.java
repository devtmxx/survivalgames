package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.game.Game;
import de.tmxx.survivalgames.module.config.MainConfig;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.user.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@InGame
@DeathMatch
public class KillListener implements Listener {
    private final FileConfiguration config;
    private final UserRegistry registry;
    private final UserBroadcaster broadcaster;
    private final Game game;

    @Inject
    KillListener(
            @MainConfig FileConfiguration config,
            UserRegistry registry,
            UserBroadcaster broadcaster,
            Game game
    ) {
        this.config = config;
        this.registry = registry;
        this.broadcaster = broadcaster;
        this.game = game;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // do not send the standard death message
        event.deathMessage(null);

        User dead = registry.getUser(event.getEntity());
        if (dead == null) return;

        // why ever spectators should die, but we don't count them as regular deaths
        if (dead.isSpectator()) return;

        handleDeath(dead);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);

        User dead = registry.getUser(event.getPlayer());
        if (dead == null) return;
        if (dead.isSpectator()) return;

        handleDeath(dead);
    }

    private void handleDeath(User dead) {
        Player deadPlayer = dead.getPlayer();
        if (config.getBoolean("lightning-on-kill", true)) {
            // strike a lightning effect at the death location. this does not harm any entity but is used to signal a
            // players death and possibly reveal the killers location to other players.
            deadPlayer.getWorld().strikeLightningEffect(deadPlayer.getLocation());
        }

        dead.setSpectator();

        User killer = null;
        if (deadPlayer.getKiller() != null) killer = registry.getUser(deadPlayer.getKiller());

        // announce the death of a player
        if (killer == null) {
            broadcaster.broadcast("kill.other", dead.getName());
        } else {
            broadcaster.broadcast("kill.by-player", dead.getName(), killer.getName());
        }

        // announce the amount of players left over
        int playersLeft = registry.getUsers(UserState.PLAYING).size();
        broadcaster.broadcast("kill.players-left." + (playersLeft == 1 ? "single" : "multiple"), playersLeft);

        // check if the game has ended
        game.checkEnd();
    }
}
