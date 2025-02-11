package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

import static de.tmxx.survivalgames.command.CommandSnippets.*;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class AddSpawnCommand implements AutoCommand {
    private final SurvivalGames plugin;

    @Override
    public String name() {
        return "addspawn";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = getUser(source);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.addspawn.help");
            return;
        }

        String id = args[0];
        Map map =  getMap(plugin.getMapManager(), id, user);
        if (map == null) return;

        if (map.addSpawnPosition(user.getPlayer().getLocation())) {
            user.sendMessage("command.addspawn.success", id, map.amountOfSpawnPositions());
        } else {
            user.sendMessage("command.common.wrong-world");
        }
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.command.addspawn";
    }
}
