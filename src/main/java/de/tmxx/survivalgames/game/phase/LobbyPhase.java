package de.tmxx.survivalgames.game.phase;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.game.GameState;
import de.tmxx.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class LobbyPhase extends GamePhase {
    public LobbyPhase(SurvivalGames plugin) {
        super(plugin, GameState.LOBBY);
    }

    @Override
    public void onStart() {
        for (World world : Bukkit.getWorlds()) {
            world.setTime(7000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setStorm(false);
            world.setThundering(false);
        }
    }

    @Override
    public void onTick() {
        if (isCounting()) {
            int timeLeft = getTimeLeft();
            String key = "timers.lobby.action-bar." + (timeLeft == 1 ? "single" : "multiple");
            User.getOnlineUsers().forEach(user -> user.getPlayer().sendActionBar(user.translate(key, timeLeft)));
        } else {
            User.getOnlineUsers().forEach(user -> user.getPlayer().sendActionBar(user.translate("timers.lobby.action-bar.waiting")));
        }
    }

    @Override
    public void onEnd() {
    }
}
