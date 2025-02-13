package de.tmxx.survivalgames.listener.ingame;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Project: survivalgames
 * 13.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(value = RegisterState.GAME, states = {
        GameState.IN_GAME,
        GameState.DEATH_MATCH
})
@RequiredArgsConstructor
public class KillListener implements Listener {
    private final SurvivalGames plugin;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // do not send the standard death message
        event.deathMessage(null);

        if (plugin.getConfig().getBoolean("lightning-on-kill", true)) {
            // strike a lightning effect at the death location. this does not harm any entity but is used to signal a
            // players death and possibly reveal the killers location to other players.
            event.getEntity().getWorld().strikeLightningEffect(event.getEntity().getLocation());
        }

        Player deadPlayer = event.getPlayer();
        User dead = User.getUser(deadPlayer);
        if (dead == null) return;

        // make the dead player a spectator
        dead.setSpectator();

        User killer = null;
        if (deadPlayer.getKiller() != null) killer = User.getUser(deadPlayer.getKiller());

        // announce the death of a player
        if (killer == null) {
            plugin.broadcast("kill.other", dead.getName());
        } else {
            plugin.broadcast("kill.by-player", dead.getName(), killer.getName());
        }

        // announce the amount of players left over
        int playersLeft = User.getUsers(UserState.PLAYING).size();
        plugin.broadcast("kill.players-left." + (playersLeft == 1 ? "single" : "multiple"), playersLeft);

        // check if the game has ended
        plugin.getGame().checkEnd();
    }
}
