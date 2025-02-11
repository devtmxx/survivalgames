package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.command.CommandSnippets;
import de.tmxx.survivalgames.map.Map;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Project: survivalgames
 * 11.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class SetSpectatorCommand implements AutoCommand {
    private final SurvivalGames plugin;

    @Override
    public String name() {
        return "setspectator";
    }

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        User user = CommandSnippets.getUser(source);
        if (user == null) return;

        if (args.length == 0) {
            user.sendMessage("command.setspectator.help");
            return;
        }

        String id = args[0];
        Map map = CommandSnippets.getMap(plugin.getMapManager(), id, user);
        if (map == null) return;

        if (map.setSpectatorSpawn(user.getPlayer().getLocation())) {
            user.sendMessage("command.setspectator.success", id);
        } else {
            user.sendMessage("command.common.wrong-world");
        }
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.command.setspectator";
    }
}
