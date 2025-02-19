package de.tmxx.survivalgames.listener.ingame;

import com.google.inject.Inject;
import de.tmxx.survivalgames.module.game.phase.DeathMatch;
import de.tmxx.survivalgames.module.game.phase.InGame;
import de.tmxx.survivalgames.module.game.phase.Starting;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Project: survivalgames
 * 19.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@Starting
@InGame
@DeathMatch
public class PlayerChatListener implements Listener {
    private final UserRegistry registry;

    @Inject
    PlayerChatListener(UserRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        User user = registry.getUser(event.getPlayer());
        if (user == null) return;

        if (user.isSpectator()) {
            event.viewers().removeIf(audience -> {
                if (!(audience instanceof Player player)) return false;

                User audienceUser = registry.getUser(player);
                if (audienceUser == null) return false;

                return !audienceUser.isSpectator();
            });
        }
    }
}
