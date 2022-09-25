package fr.anxxitty.srp;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Logger;

public class SpeedRunPlugin extends JavaPlugin {

    private final static int requiresProtocol = 24;

    private Logger logger;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.logger = Bukkit.getLogger();

        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        MultiverseNetherPortals netherPortals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");

        if (core == null) {
            logger.info("[SpeedRunPlugin] Multiverse-Core not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (netherPortals == null) {
            logger.info("[SpeedRunPlugin] Multiverse-NetherPortals not found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Check if the version of Multiverse-Core is correct
        if (core.getProtocolVersion() < requiresProtocol) {
            logger.severe("Your Multiverse-Core is OUT OF DATE");
            logger.severe("This version of SpeedRunPlugin requires Protocol Level: " + requiresProtocol);
            logger.severe("Your of Core Protocol Level is: " + core.getProtocolVersion());
            logger.severe("Grab an updated copy at: ");
            logger.severe("http://dev.bukkit.org/bukkit-plugins/multiverse-core/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        MVWorldManager worldManager = core.getMVWorldManager();
        SRPListener pluginListener = new SRPListener(core, this);

        //registers the commands
        Objects.requireNonNull(getCommand("reset")).setExecutor(new Commands(core, netherPortals));
        Objects.requireNonNull(getCommand("startrun")).setExecutor(new Commands(core, netherPortals));
        Objects.requireNonNull(getCommand("endrun")).setExecutor(new Commands(core, netherPortals));
        getServer().getPluginManager().registerEvents(pluginListener, this);

        //Check if the worlds are created and regen them if needed

        //Unload useless worlds
        worldManager.unloadWorld("world_nether");
        worldManager.unloadWorld("world_the_end");

        logger.info("[SpeedRunPlugin] The plugin has started successfully!");
    }

    @Override
    public void onDisable() {
        logger.info("[SpeedRunPlugin] The plugin has stopped successfully!");
    }

}
