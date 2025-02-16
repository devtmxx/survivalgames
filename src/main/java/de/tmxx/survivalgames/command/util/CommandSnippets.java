package de.tmxx.survivalgames.command.util;

import com.google.common.base.Joiner;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.map.MapManager;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * <p>
 *     These command snippets are repeatedly used by different commands. This is used to prevent duplicate code
 *     fragments within the command code base.
 * </p>
 *
 * @author timmauersberger
 * @version 1.0
 */
public final class CommandSnippets {
    /**
     * Retrieves a user from a {@link CommandSourceStack} or null if the {@link org.bukkit.command.CommandSender} is
     * not a player or the player is not yet registered as a user.
     *
     * @param stack the command source stack
     * @param registry the user registry
     * @return the user or null
     */
    public static User getUser(CommandSourceStack stack, UserRegistry registry) {
        if (!(stack.getSender() instanceof Player player)) return null;
        return registry.getUser(player);
    }

    /**
     * Retrieves a map for a specified map id. This will send an error message to the user if there is no known map for
     * the specified map id.
     *
     * @param manager the map manager to use
     * @param id the id of the requested map
     * @param user the user
     * @return the requested map or null
     */
    public static Map getMap(MapManager manager, String id, User user) {
        Map map = manager.get(id);
        if (map == null) {
            user.sendMessage("command.common.map-not-found");
            return null;
        }
        return map;
    }

    /**
     * Summarizes an array of arguments to one string, starting at a specific argument.
     *
     * @param start the start index
     * @param args the arguments
     * @return the built string
     */
    public static String sumArgs(int start, String[] args) {
        String[] toSum = new String[args.length - start];
        System.arraycopy(args, start, toSum, 0, toSum.length);
        return Joiner.on(" ").join(toSum);
    }
}
