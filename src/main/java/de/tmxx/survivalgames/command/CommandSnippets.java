package de.tmxx.survivalgames.command;

import com.google.common.base.Joiner;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public final class CommandSnippets {
    public static User getUser(CommandSourceStack stack) {
        if (!(stack.getSender() instanceof Player player)) return null;
        return User.getUser(player);
    }

    public static Map getMap(MapManager manager, String id, User user) {
        Map map = manager.get(id);
        if (map == null) {
            user.sendMessage("command.common.map-not-found");
            return null;
        }
        return map;
    }

    public static String sumArgs(int start, String[] args) {
        String[] toSum = new String[args.length - start];
        System.arraycopy(args, start, toSum, 0, toSum.length);
        return Joiner.on(" ").join(toSum);
    }
}
