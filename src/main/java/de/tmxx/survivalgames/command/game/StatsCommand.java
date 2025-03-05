package de.tmxx.survivalgames.command.game;

import com.google.inject.Inject;
import de.tmxx.survivalgames.command.Command;
import de.tmxx.survivalgames.stats.Stats;
import de.tmxx.survivalgames.stats.StatsKey;
import de.tmxx.survivalgames.stats.StatsService;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserRegistry;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.NumberFormat;

import static de.tmxx.survivalgames.command.util.CommandSnippets.*;

/**
 * Project: survivalgames
 * 27.02.25
 *
 * @author timmauersberger
 * @version 1.0
 */
@de.tmxx.survivalgames.command.Game
public class StatsCommand implements Command {
    private final JavaPlugin plugin;
    private final UserRegistry registry;
    private final StatsService statsService;

    @Inject
    StatsCommand(JavaPlugin plugin, UserRegistry registry, StatsService statsService) {
        this.plugin = plugin;
        this.registry = registry;
        this.statsService = statsService;
    }

    @Override
    public String name() {
        return "stats";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source, registry);
        if (user == null) return;

        if (args.length == 0) {
            user.retrieveStats(stats -> showStats(user, user.getName(), stats));
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                String name = args[0];
                Stats stats = statsService.loadStats(name);
                if (stats == null) {
                    user.sendMessage("command.stats.not-found", name);
                    return;
                }

                showStats(user, name, stats);
            });
        }
    }

    private void showStats(User user, String playerName, Stats stats) {
        int kills = stats.get(StatsKey.KILLS);
        int deaths = stats.get(StatsKey.DEATHS);
        int wins = stats.get(StatsKey.WINS);
        int gamesPlayed = stats.get(StatsKey.GAMES_PLAYED);
        float killDeathRation = calculateRatio(kills, deaths);
        float winChance = calculateRatio(wins, gamesPlayed) * 100;

        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(2);

        user.sendMessage(
                "command.stats.info",
                playerName,
                kills,
                deaths,
                format.format(killDeathRation),
                wins,
                gamesPlayed,
                format.format(winChance)
        );
    }

    private float calculateRatio(int numerator, int denominator) {
        return denominator == 0 ? numerator : (float) numerator / denominator;
    }
}
