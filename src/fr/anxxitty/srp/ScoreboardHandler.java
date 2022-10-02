package fr.anxxitty.srp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Objects;

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
            if (Bukkit.getOnlinePlayers().contains(player)) {
                String scoreTitle = Objects.requireNonNull(player.getScoreboard().getObjective(DisplaySlot.SIDEBAR)).getDisplayName();
                player.getScoreboard().resetScores(scoreTitle);

                Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
                player.setScoreboard(scoreboard);
                ScoreboardHandler.createScoreboard(scoreboardManager, player, title, text);
            }
    }

    public static void clearScoreboard(ScoreboardManager scoreboardManager, Player player) {
        Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
        player.setScoreboard(scoreboard);
    }

}
