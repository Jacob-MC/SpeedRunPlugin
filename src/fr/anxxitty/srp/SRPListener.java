package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

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

            Objects.requireNonNull(player).sendTitle("§aYou win!", "§aYou have killed the Ender Dragon!",10, 140, 20);

            Bukkit.getScheduler().runTaskLater(SpeedRunPlugin, () -> player.sendTitle("§4Run Completed", "§4Deleting Worlds.",10, 140, 20), 250);

            Bukkit.getScheduler().runTaskLater(SpeedRunPlugin, () -> WorldHandler.deleteWorlds(worldManager, player), 400);
        }
    }

    public void onJoin(PlayerJoinEvent event) {

        String resetonstarting = SpeedRunPlugin.getConfig().getString("resetonstarting");

        try {
            //resets player data only if the map is regenerated at server startup
            if (Objects.requireNonNull(resetonstarting).equalsIgnoreCase("true")) {

                Player player = event.getPlayer();

                //Kills the dragon if the player is in the end because otherwise there's a bug with the bossbar
                //I don't like the usage of dispatchCommand function so if you know an other way to do it, please let me know
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=minecraft:ender_dragon]");

                //Removes all the data about the player (potion effect, health, inventory, advancements...)
                WorldHandler.playerHandler(player);

                if (player.getWorld() != this.core.getMVWorldManager().getMVWorld("spworld-" + player.getUniqueId())) {
                    Location location = this.core.getMVWorldManager().getMVWorld("spworld-" + player.getUniqueId()).getSpawnLocation();
                    location = this.core.getSafeTTeleporter().getSafeLocation(location);
                    player.teleport(location);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

}
