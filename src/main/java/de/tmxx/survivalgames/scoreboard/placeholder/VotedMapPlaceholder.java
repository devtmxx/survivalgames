package de.tmxx.survivalgames.scoreboard.placeholder;

import com.google.inject.Inject;
import de.tmxx.survivalgames.i18n.I18n;
import de.tmxx.survivalgames.map.MapManager;

import java.util.Locale;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class VotedMapPlaceholder implements Placeholder {
    private final MapManager mapManager;
    private final I18n i18n;

    @Inject
    VotedMapPlaceholder(MapManager mapManager, I18n i18n) {
        this.mapManager = mapManager;
        this.i18n = i18n;
    }
    @Override
    public String variable() {
        return "%voted-map%";
    }

    @Override
    public String replacement(Locale locale) {
        return mapManager.hasVotingEnded() ? mapManager.getVotedMap().getName() : i18n.translateRaw(locale, "placeholder.no-voted-map");
    }
}
