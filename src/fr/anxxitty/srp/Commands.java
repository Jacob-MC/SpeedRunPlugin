package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {
    private final MVWorldManager worldManager;
    private final MultiverseNetherPortals netherPortals;

    SpeedRunPlugin speedRunPlugin = SpeedRunPlugin.getPlugin(SpeedRunPlugin.class);

    public Commands(MultiverseCore core, MultiverseNetherPortals netherPortals) {
        this.worldManager = core.getMVWorldManager();
        this.netherPortals = netherPortals;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] strings) {
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

                    //deletes worlds
                    WorldHandler.deleteWorlds(worldManager, player);
                    //clears stats, inventory, etc
                    WorldHandler.playerHandler(player);
                    //generates world
                    WorldHandler.worldGenerator(player, overworldname, nethername, endname, worldManager);
                    //links the worlds
                    WorldHandler.addLinks(netherPortals, overworldname, nethername, endname);

                }
                return true;
            }

            if (cmd.getName().equalsIgnoreCase("endrun")) {
                //Checks if the speedrun world exists
                if (worldManager.getMVWorlds().toString().contains(overworldname)) {
                    player.sendMessage("§3Ending speedrun!");
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

                    //deletes worlds
                    WorldHandler.deleteWorlds(worldManager, player);
                    //clears stats, inventory, etc
                    WorldHandler.playerHandler(player);
                    //generates world
                    WorldHandler.worldGenerator(player, overworldname, nethername, endname, worldManager);
                    //links the worlds
                    WorldHandler.addLinks(netherPortals, overworldname, nethername, endname);

                } else {
                    player.sendMessage("§cThere is no speedrun to reset!");
                }
                return true;
            }
        }
        else {
            sender.sendMessage("Cannot reset from the console!");
        }
        return false;
    }
}
