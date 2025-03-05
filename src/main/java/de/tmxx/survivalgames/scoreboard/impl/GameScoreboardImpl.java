package de.tmxx.survivalgames.scoreboard.impl;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.tmxx.survivalgames.game.GamePhaseChanger;
import de.tmxx.survivalgames.game.phase.GamePhase;
import de.tmxx.survivalgames.scoreboard.GameScoreboard;
import de.tmxx.survivalgames.scoreboard.placeholder.PlaceholderApplier;
import de.tmxx.survivalgames.user.User;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: survivalgames
 * 05.03.2025
 *
 * @author timmauersberger
 * @version 1.0
 */
public class GameScoreboardImpl implements GameScoreboard {
    private final GamePhaseChanger gamePhaseChanger;
    private final PlaceholderApplier placeholderApplier;
    private final User user;
    private final Scoreboard scoreboard;
    private final List<String> scores = new ArrayList<>();

    private Objective objective;

    @Inject
    GameScoreboardImpl(GamePhaseChanger gamePhaseChanger, PlaceholderApplier placeholderApplier, @Assisted User user) {
        this.gamePhaseChanger = gamePhaseChanger;
        this.placeholderApplier = placeholderApplier;
        this.user = user;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        user.getPlayer().setScoreboard(scoreboard);
    }

    @Override
    public void setup() {
        objective = scoreboard.registerNewObjective("survivalgames", Criteria.DUMMY, user.translate("scoreboard.title"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        loadScores();
        setScores();
    }

    @Override
    public void update() {
        if (objective == null) return;

        setScores();
    }

    @Override
    public void reset() {
        scores.clear();
        objective.unregister();
    }

    private void loadScores() {
        GamePhase currentPhase = gamePhaseChanger.currentPhase();
        if (currentPhase == null) return;

        scores.addAll(currentPhase.scoreboardScores());
    }

    private void setScores() {
        if (objective == null) return;

        for (int i = 1; i <= scores.size(); i++) {
            String key = scores.get(scores.size() - i);
            getScore(i).customName(placeholderApplier.apply(user.translateRaw(key), user.getPlayer().locale()));
        }
    }

    private Score getScore(int value) {
        Score score = objective.getScore("score_" + value);
        score.setScore(value);
        return score;
    }
}
