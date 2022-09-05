package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class SRPListener implements Listener {
    
    private final MultiverseCore core;
    private final SpeedRunPlugin SpeedRunPlugin;
    private final MVWorldManager worldManager;

    public SRPListener(MultiverseCore core, SpeedRunPlugin SpeedRunPlugin) {
        this.core = core;
        this.worldManager = core.getMVWorldManager();
        this.SpeedRunPlugin = SpeedRunPlugin;
    }

    @EventHandler
    //listens for the dragon death
    public void onDragonDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        Player player = entity.getKiller();
        if (entity.getType() == EntityType.ENDER_DRAGON) {
            long end = System.currentTimeMillis();
            //get the start variable from the commands class
            long start = Commands.start;
            //calculate the time of completion
            long seconds = (end - start) / 1000;

            long HH = seconds / 3600;
            long MM = (seconds % 3600) / 60;
            long SS = seconds % 60;
            String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);

            player.sendTitle("§aYou win!", "§aYou have killed the Ender Dragon in " + timeInHHMMSS + "!",10, 140, 20);

            Bukkit.getScheduler().runTaskLater(SpeedRunPlugin, () -> {
                player.sendTitle("§4Run Completed", "§4Deleting Worlds.",10, 140, 20);
            }, 250);

            //Deletes the worlds automatically after the dragon is killed
            Bukkit.getScheduler().runTaskLater(SpeedRunPlugin, () -> {
                worldManager.deleteWorld("spworld");
                worldManager.deleteWorld("spnether");
                worldManager.deleteWorld("spend");
            }, 400);
        }
    }

    public void onJoin(PlayerJoinEvent event) {

        String resetonstarting = SpeedRunPlugin.getConfig().getString("resetonstarting");

        try {
            //resets player data only if the map is regenerated at server startup
            if (resetonstarting.equalsIgnoreCase("true")) {

                Player player = event.getPlayer();

                //Kills the dragon if the player is in the end because otherwise there's a bug with the bossbar
                //I don't like the usage of dispatchCommand function so if you know an other way to do it, please let me know
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

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

                if (player.getWorld() != this.core.getMVWorldManager().getMVWorld("spworld")) {
                    Location location = this.core.getMVWorldManager().getMVWorld("spworld").getSpawnLocation();
                    location = this.core.getSafeTTeleporter().getSafeLocation(location);
                    player.teleport(location);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
