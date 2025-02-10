package de.tmxx.survivalgames.command.setup;

import de.tmxx.survivalgames.SurvivalGames;
import de.tmxx.survivalgames.auto.AutoCommand;
import de.tmxx.survivalgames.auto.AutoRegister;
import de.tmxx.survivalgames.auto.RegisterState;
import de.tmxx.survivalgames.user.User;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
@AutoRegister(RegisterState.SETUP)
@RequiredArgsConstructor
public class SetLobbyCommand implements AutoCommand {
    private final SurvivalGames survivalGames;

    @Override
    public void execute(CommandSourceStack source, String[] args) {
        if (!(source.getSender() instanceof Player player)) return;

        User user = User.getUser(player);
        if (user == null) return;

        survivalGames.getConfig().set("lobby.spawn", player.getLocation());
        survivalGames.saveConfig();
        user.sendMessage("command.setlobby.success");
    }

    @Override
    public @Nullable String permission() {
        return "survivalgames.command.setlobby";
    }

    @Override
    public String name() {
        return "setlobby";
    }
}
