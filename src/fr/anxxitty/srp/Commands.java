package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class Commands implements CommandExecutor {
    public static long timer;

    public static Scoreboard scoreboard;
    private final MultiverseCore core;
    private final MVWorldManager worldManager;

    SpeedRunPlugin speedRunPlugin = SpeedRunPlugin.getPlugin(SpeedRunPlugin.class);

    public Commands(MultiverseCore core) {
        this.core = core;
        this.worldManager = core.getMVWorldManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

            scoreboard.registerNewTeam("player");
            scoreboard.getTeam("player").addEntry(player.getName());
            player.setScoreboard(scoreboard);

            if (cmd.getName().equalsIgnoreCase("startrun")) {
                // Checks if speedrun world exists
                if (worldManager.getMVWorlds().toString().contains("spworld")) {
                    player.sendMessage("§cYou are already in a speedrun! Use the reset command to reset the world, or the endrun command to end the run.");
                } else {
                    //deletes worlds
                    worldManager.deleteWorld("spworld");
                    worldManager.deleteWorld("spnether");
                    worldManager.deleteWorld("spend");

                    player.sendMessage("§3Starting speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

                    //Removes all the data about the player (potion effect, health, inventory, advancements...)
                    player.getInventory().clear();
                    player.updateInventory();
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    player.setExp(0);
                    player.setLevel(0);
                    player.setSaturation(20);

                    Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
                    while (iterator.hasNext())
                    {
                        AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
                        for (String criteria : progress.getAwardedCriteria())
                            progress.revokeCriteria(criteria);
                    }

                    //Regenerates the worlds
                    worldManager.addWorld("spworld", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
                    String seed = String.valueOf(worldManager.getMVWorld("spworld").getSeed());
                    worldManager.addWorld("spnether", World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
                    worldManager.addWorld("spend", World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

                    worldManager.getMVWorld("spnether").setRespawnToWorld("spworld");
                    worldManager.getMVWorld("spend").setRespawnToWorld("spworld");

                    //Teleports the player at a safe location
                    Location originalLocation = worldManager.getMVWorld("spworld").getSpawnLocation();
                    Location safeLocation = core.getSafeTTeleporter().getSafeLocation(originalLocation);
                    worldManager.getMVWorld("spworld").setSpawnLocation(safeLocation);

                    player.sendMessage("§3Generation finished!");
                    player.sendMessage("§3Teleporting...");

                    player.teleport(safeLocation);

                    timer = 0;

                    //Starts the timer

                    Objective time = scoreboard.registerNewObjective("Time", "dummy");

                    long HH = timer / 3600;
                    long MM = (timer % 3600) / 60;
                    long SS = timer % 60;
                    String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);

                    time.setDisplaySlot(DisplaySlot.SIDEBAR);

                    time.setDisplayName(ChatColor.GREEN + "Time:");
                    Objective time1 = scoreboard.registerNewObjective("Time1", "dummy");

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(speedRunPlugin, new Runnable() {
                        @Override
                        public void run() {
                            timer++;
                            time1.setDisplayName(ChatColor.GREEN + timeInHHMMSS);
                        }
                    }, 0, 20);

                    player.sendMessage("§3Teleported!");

                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("endrun")) {
                //Checks if the speedrun world exists
                if (worldManager.getMVWorlds().toString().contains("spworld")) {
                    player.sendMessage("§3Ending speedrun!");
                    //deletes worlds
                    Bukkit.getScheduler().runTaskLater(speedRunPlugin, () -> {
                        worldManager.deleteWorld("spworld");
                        worldManager.deleteWorld("spnether");
                        worldManager.deleteWorld("spend");
                        Bukkit.getScheduler().cancelTasks(speedRunPlugin);
                        timer = 0;
                        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
                        player.sendMessage("§3Speedrun ended!");
                    }, 100);
                    return true;
                } else {
                    player.sendMessage("§cThere is no speedrun to end!");
                    return true;
                }
            }

            if (cmd.getName().equalsIgnoreCase("reset")) {
                //Checks if the speedrun world exists
                if (worldManager.getMVWorlds().toString().contains("spworld")) {
                    player.sendMessage("§3Resetting speedrun! §6/!\\ §4Please don't move during the generation, this may crash the server §6/!\\");

                    //Resets timer
                    Bukkit.getScheduler().cancelTasks(speedRunPlugin);
                    timer = 0;

                    //Removes all the data about the player (potion effect, health, inventory, advancements...)
                    player.getInventory().clear();
                    player.updateInventory();
                    player.setHealth(20.0);
                    player.setFoodLevel(20);
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    player.setExp(0);
                    player.setLevel(0);
                    player.setSaturation(20);

                    Iterator<Advancement> iterator = Bukkit.getServer().advancementIterator();
                    while (iterator.hasNext())
                    {
                        AdvancementProgress progress = player.getAdvancementProgress(iterator.next());
                        for (String criteria : progress.getAwardedCriteria())
                            progress.revokeCriteria(criteria);
                    }
                    //Regenerates the worlds
                    worldManager.deleteWorld("spworld");
                    worldManager.deleteWorld("spnether");
                    worldManager.deleteWorld("spend");

                    worldManager.addWorld("spworld", World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
                    String seed = String.valueOf(worldManager.getMVWorld("spworld").getSeed());
                    worldManager.addWorld("spnether", World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
                    worldManager.addWorld("spend", World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

                    worldManager.getMVWorld("spnether").setRespawnToWorld("spworld");
                    worldManager.getMVWorld("spend").setRespawnToWorld("spworld");

                    //Teleports the player at a safe location
                    Location originalLocation = worldManager.getMVWorld("spworld").getSpawnLocation();
                    Location safeLocation = core.getSafeTTeleporter().getSafeLocation(originalLocation);
                    worldManager.getMVWorld("spworld").setSpawnLocation(safeLocation);

                    player.sendMessage("§3Generation finished!");
                    player.sendMessage("§3Teleporting...");

                    player.teleport(safeLocation);

                    //Starts the timer

                    Objective time = scoreboard.registerNewObjective("Time", "dummy");

                    long HH = timer / 3600;
                    long MM = (timer % 3600) / 60;
                    long SS = timer % 60;
                    String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);

                    time.setDisplaySlot(DisplaySlot.SIDEBAR);

                    time.setDisplayName(ChatColor.GREEN + "Time:");
                    Objective time1 = scoreboard.registerNewObjective("Time1", "dummy");

                    Bukkit.getScheduler().scheduleSyncRepeatingTask(speedRunPlugin, new Runnable() {
                        @Override
                        public void run() {
                            timer++;
                            time1.setDisplayName(ChatColor.GREEN + timeInHHMMSS);
                        }
                    }, 0, 20);

                    player.sendMessage("§3Teleported!");

                    return true;
                } else {
                    player.sendMessage("§cThere is no speedrun to reset!");
                    return true;
                }
            }
        }
        else {
            sender.sendMessage("Cannot reset from the console!");
        }
        return false;
    }
}
