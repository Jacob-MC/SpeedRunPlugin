package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.apache.commons.lang.time.StopWatch;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    private final MVWorldManager worldManager;
    private final MultiverseNetherPortals netherPortals;
    private final StopWatch stopWatch;

    private final ScoreboardManager scoreboardManager;

    SpeedRunPlugin speedRunPlugin = SpeedRunPlugin.getPlugin(SpeedRunPlugin.class);

    public Commands(MultiverseCore core, MultiverseNetherPortals netherPortals, StopWatch stopWatch, ScoreboardManager scoreboardManager) {
        this.worldManager = core.getMVWorldManager();
        this.netherPortals = netherPortals;
        this.stopWatch = stopWatch;
        this.scoreboardManager = scoreboardManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            String overworldname = "spworld-" + uuid;
            String nethername = "spnether-" + uuid;
            String endname = "spend-" + uuid;

            if (cmd.getName().equalsIgnoreCase("startrun")) {
                // Checks if speedrun world exists
                if (worldManager.getMVWorlds().toString().contains(overworldname)) {
                    player.sendMessage("§cYou are already in a speedrun! Use the reset command to reset the world, or the endrun command to end the run.");
                } else {

                    player.sendMessage("§3Starting speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

                    //clears stats, inventory, etc
                    WorldHandler.playerHandler(player);
                    //generates world
                    WorldHandler.worldGenerator(player, overworldname, nethername, endname, worldManager);
                    //links the worlds
                    WorldHandler.addLinks(netherPortals, overworldname, nethername, endname);
                    //starts the timer
                    StopWatchHandler.startTimer(stopWatch);
                    //creates scoreboard
                    ScoreboardHandler.createScoreboard(scoreboardManager, player, "§3Timer", StopWatchHandler.checkTime(stopWatch));

                    //refreshes the scoreboard to update the time
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(speedRunPlugin, () -> ScoreboardHandler.refreshScoreboard(scoreboardManager, player, "§3Timer", StopWatchHandler.checkTime(stopWatch)), 0, 1);

                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("endrun")) {
                //Checks if the speedrun world exists
                if (worldManager.getMVWorlds().toString().contains(overworldname)) {
                    player.sendMessage("§3Ending speedrun!");
                    //stops update task
                    Bukkit.getScheduler().cancelTasks(speedRunPlugin);
                    //stops and resets timer
                    StopWatchHandler.stopTimer(stopWatch);
                    StopWatchHandler.resetTimer(stopWatch);
                    //clears scoreboard
                    ScoreboardHandler.clearScoreboard(scoreboardManager, player);
                    //deletes worlds
                    Bukkit.getScheduler().runTaskLater(speedRunPlugin, () -> {
                        WorldHandler.deleteWorlds(worldManager, player);
                        player.sendMessage("§3Speedrun ended!");
                    }, 100);
                } else {
                    player.sendMessage("§cThere is no speedrun to end!");
                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("reset")) {
                //Checks if the speedrun world exists
                if (worldManager.getMVWorlds().toString().contains(overworldname)) {
                    player.sendMessage("§3Resetting speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

                    //stops update task
                    Bukkit.getScheduler().cancelTasks(speedRunPlugin);
                    //stops and resets timer
                    StopWatchHandler.stopTimer(stopWatch);
                    StopWatchHandler.resetTimer(stopWatch);
                    //clears scoreboard
                    ScoreboardHandler.clearScoreboard(scoreboardManager, player);
                    //deletes worlds
                    WorldHandler.deleteWorlds(worldManager, player);
                    //clears stats, inventory, etc
                    WorldHandler.playerHandler(player);
                    //generates world
                    WorldHandler.worldGenerator(player, overworldname, nethername, endname, worldManager);
                    //links the worlds
                    WorldHandler.addLinks(netherPortals, overworldname, nethername, endname);
                    //starts the timer
                    StopWatchHandler.startTimer(stopWatch);
                    //creates scoreboard
                    ScoreboardHandler.createScoreboard(scoreboardManager, player, "§3Timer", StopWatchHandler.checkTime(stopWatch));

                    //refreshes the scoreboard to update the time
                    Bukkit.getScheduler().scheduleSyncRepeatingTask(speedRunPlugin, () -> ScoreboardHandler.refreshScoreboard(scoreboardManager, player, "§3Timer", StopWatchHandler.checkTime(stopWatch)), 0, 1);

                } else {
                    player.sendMessage("§cThere is no speedrun to reset!");
                }
                return true;
            }

        }
        else {
            sender.sendMessage("Commands cannot be used from the console!");
        }
        return false;
    }
}
