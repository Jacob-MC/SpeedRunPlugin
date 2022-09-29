package fr.anxxitty.srp;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class ScoreboardHandler {

    public static void createScoreboard(ScoreboardManager scoreboardManager, Player player, String title, String text) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(title, "dummy", title);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = objective.getScore(text);
        score.setScore(1);

        player.setScoreboard(scoreboard);
    }

    public static void refreshScoreboard(ScoreboardManager scoreboardManager, Player player, String title, String text) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
        ScoreboardHandler.createScoreboard(scoreboardManager, player, title, text);
    }

    public static void clearScoreboard(ScoreboardManager scoreboardManager, Player player) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
    }

}
