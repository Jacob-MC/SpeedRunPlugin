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
    }

    public static void worldGenerator(Player player, String overworldname, String nethername, String endname, MVWorldManager worldManager) {
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
        worldManager.deleteWorld("spworld-" + player.getUniqueId());
        worldManager.deleteWorld("spnether-" + player.getUniqueId());
        worldManager.deleteWorld("spend-" + player.getUniqueId());
    }

    public static void addLinks(MultiverseNetherPortals portals, String s, String s1, String s2) {
        portals.addWorldLink(s, s1, PortalType.NETHER);
        portals.addWorldLink(s1, s, PortalType.NETHER);
        portals.addWorldLink(s, s2, PortalType.ENDER);
        portals.addWorldLink(s2, s, PortalType.ENDER);
    }
}
