package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class WorldHandler {

    public static void playerHandler(Player player) {
        //clears all the player's stats
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
    }

    public static void worldGenerator(Player player, String overworldname, String nethername, String endname, MVWorldManager worldManager) {
        //generates the worlds and teleports the player
        worldManager.addWorld(overworldname, World.Environment.NORMAL, null, WorldType.NORMAL, true, null);
        String seed = String.valueOf(worldManager.getMVWorld(overworldname).getSeed());
        worldManager.addWorld(nethername, World.Environment.NETHER, seed, WorldType.NORMAL, true, null);
        worldManager.addWorld(endname, World.Environment.THE_END, seed, WorldType.NORMAL, true, null);

        worldManager.getMVWorld(nethername).setRespawnToWorld(overworldname);
        worldManager.getMVWorld(endname).setRespawnToWorld(overworldname);


        Location location = worldManager.getMVWorld(overworldname).getSpawnLocation();
        worldManager.getMVWorld(overworldname).setSpawnLocation(location);

        player.sendMessage("§3Generation finished!");
        player.sendMessage("§3Teleporting...");

        player.teleport(location);

        player.sendMessage("§3Teleported!");
    }

    public static void deleteWorlds(MVWorldManager worldManager, Player player) {
        //deletes the worlds
        worldManager.deleteWorld("spworld-" + player.getUniqueId());
        worldManager.deleteWorld("spnether-" + player.getUniqueId());
        worldManager.deleteWorld("spend-" + player.getUniqueId());
    }

   public static void regenWorlds(MVWorldManager worldManager, Player player) {
        //regenerates the worlds
        worldManager.regenWorld("spworld-" + player.getUniqueId(), true, true, "", true);
        worldManager.regenWorld("spnether-" + player.getUniqueId(), true, true, "", true);
        worldManager.regenWorld("spend-" + player.getUniqueId(), true, true, "", true);
    }

    public static void clearInv(Player player) {
        //clears the player's inventory
        player.getInventory().clear();
        player.updateInventory();
    }

    public static void addLinks(MultiverseNetherPortals portals, String s, String s1, String s2) {
        //links the worlds
        portals.addWorldLink(s, s1, PortalType.NETHER);
        portals.addWorldLink(s1, s, PortalType.NETHER);
        portals.addWorldLink(s, s2, PortalType.ENDER);
        portals.addWorldLink(s2, s, PortalType.ENDER);
    }
}
