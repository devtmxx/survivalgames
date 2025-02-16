package de.tmxx.survivalgames.user.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.user.User;
import de.tmxx.survivalgames.user.UserState;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Project: survivalgames
 * 10.02.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class UserImpl implements User {
    private final I18n i18n;
    @Getter private final UUID uniqueId;
    @Getter private final String name;
    @Getter private final Player player;

    @Getter @Setter private UserState state = UserState.PLAYING;

    @Inject
    public UserImpl(I18n i18n, @Assisted Player player) {
        this.i18n = i18n;
        this.player = player;
        uniqueId = player.getUniqueId();
        name = player.getName();
    }

    @Override
    public void sendMessage(String key, Object... args) {
        player.sendMessage(translate(key, args));
    }

    @Override
    public Component translate(String key, Object... args) {
        if (key == null) return Component.empty();
        return i18n.translate(player.locale(), key, args);
    }

    @Override
    public List<Component> translateList(String key, Object... args) {
        return i18n.translateList(player.locale(), key, args);
    }

    @Override
    public boolean isSpectator() {
        return state.equals(UserState.SPECTATING);
    }
}
