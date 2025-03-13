package de.tmxx.survivalgames.listener.pregame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.module.config.MaxPlayers;
import de.tmxx.survivalgames.module.game.phase.Lobby;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.kicker.UserKicker;
import de.tmxx.survivalgames.user.UserRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Project: survivalgames
 * 17.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@Lobby
public class PremiumLoginListener implements Listener {
    private final int maxPlayers;
    private final UserRegistry registry;
    private final I18n i18n;
    private final UserKicker kicker;

    @Inject
    PremiumLoginListener(@MaxPlayers int maxPlayers, UserRegistry registry, I18n i18n, UserKicker kicker) {
        this.maxPlayers = maxPlayers;
        this.registry = registry;
        this.i18n = i18n;
        this.kicker = kicker;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        int online = Bukkit.getOnlinePlayers().size();

        // do nothing if the server is not yet full
        if (online < maxPlayers) return;

        Player player = event.getPlayer();
        User user = registry.getUser(player);

        // an error occurred if there is no user registered for the player at this point. kicking the user to prevent
        // unintended behaviour of the game later on
        if (user == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, i18n.translate(player.locale(), "kick.error"));
            return;
        }

        // do not allow login if the server is full and the player does not have priority
        if (!player.hasPermission("survivalgames.priority")) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, user.translate("kick.full"));
            return;
        }

        // cannot kick a player because every player online has priority status
        if (!kicker.kick()) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, user.translate("kick.full-priority"));
            return;
        }

        event.allow();
    }
}
