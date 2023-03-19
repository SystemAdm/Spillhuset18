package com.spillhuset.oddjob;

import com.spillhuset.oddjob.Commands.Currency.BalanceCommand;
import com.spillhuset.oddjob.Commands.Currency.CurrencyCommand;
import com.spillhuset.oddjob.Commands.Currency.PayCommand;
import com.spillhuset.oddjob.Commands.Currency.TransferCommand;
import com.spillhuset.oddjob.Commands.Essentials.FeedCommand;
import com.spillhuset.oddjob.Commands.Essentials.HealCommand;
import com.spillhuset.oddjob.Commands.Essentials.LoadedCommand;
import com.spillhuset.oddjob.Commands.Essentials.SuicideCommand;
import com.spillhuset.oddjob.Commands.Guilds.GuildsCommand;
import com.spillhuset.oddjob.Commands.Homes.HomesCommand;
import com.spillhuset.oddjob.Commands.Locks.LocksCommand;
import com.spillhuset.oddjob.Commands.Profession.ProfessionCommand;
import com.spillhuset.oddjob.Commands.Shops.ShopsCommand;
import com.spillhuset.oddjob.Commands.Shops.TradeCommand;
import com.spillhuset.oddjob.Commands.Teleport.TeleportCommand;
import com.spillhuset.oddjob.Commands.Teleport.TeleportTPACommand;
import com.spillhuset.oddjob.Commands.Warps.WarpCommand;
import com.spillhuset.oddjob.Events.*;
import com.spillhuset.oddjob.Managers.*;
import com.spillhuset.oddjob.Utils.GMIHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

public class OddJob extends JavaPlugin {
    private static OddJob instance;
    private HomesManager homesManager;
    private PlayerManager playerManager;
    private CurrencyManager currencyManager;
    private GuildsManager guildsManager;
    private ArenaManager arenaManager;
    private AuctionsManager auctionsManager;
    private LocksManager locksManager;
    private ShopsManager shopsManager;
    private TeleportManager teleportManager;
    private WarpsManager warpsManager;
    private GMIHandler gmiHandler;
    private GameManager gameManager;

    public static OddJob getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        ConfigManager.load();
        playerManager = new PlayerManager();

        /* Homes */
        if (ConfigManager.getBoolean("plugin.homes")) {
            homesManager = new HomesManager();
            getCommand("homes").setExecutor(new HomesCommand());
        }

        /* Currency */
        if (ConfigManager.getBoolean("plugin.currency")) {
            currencyManager = new CurrencyManager();
            getCommand("transfer").setExecutor(new TransferCommand());
            getCommand("pay").setExecutor(new PayCommand());
            getCommand("balance").setExecutor(new BalanceCommand());
            getCommand("currency").setExecutor(new CurrencyCommand());
        }

        /* Guilds */
        if (ConfigManager.getBoolean("plugin.guilds")) {
            guildsManager = new GuildsManager();
            guildsManager.load();
            getCommand("guilds").setExecutor(new GuildsCommand());
        }

        /* Arena */
        if (ConfigManager.getBoolean("plugin.games")) {
            arenaManager = new ArenaManager();
            gameManager = new GameManager();
        }

        /* Auctions */
        if (ConfigManager.getBoolean("plugin.auctions")) auctionsManager = new AuctionsManager();

        /* Locks */
        if (ConfigManager.getBoolean("plugin.locks")) {
            locksManager = new LocksManager();
            getCommand("locks").setExecutor(new LocksCommand());
        }

        /* Shops */
        if (ConfigManager.getBoolean("plugin.shops")) {
            shopsManager = new ShopsManager();
            getCommand("shops").setExecutor(new ShopsCommand());
            getCommand("trade").setExecutor(new TradeCommand());
        }

        /* Teleport */
        if (ConfigManager.getBoolean("plugin.teleport")) {
            teleportManager = new TeleportManager();
            getCommand("teleports").setExecutor(new TeleportCommand());
            getCommand("tpa").setExecutor(new TeleportTPACommand());
        }

        /* Warps*/
        if (ConfigManager.getBoolean("plugin.warps")) {
            warpsManager = new WarpsManager();
            getCommand("warps").setExecutor(new WarpCommand());
        }

        getCommand("professions").setExecutor(new ProfessionCommand());

        /* Essentials */
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("heal").setExecutor(new HealCommand());

        /* Misc */
        getCommand("loaded").setExecutor(new LoadedCommand());

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new OnBlockBreakEvent(), this);
        pm.registerEvents(new OnBlockFromToEvent(), this);
        pm.registerEvents(new OnBlockPlaceEvent(), this);

        pm.registerEvents(new OnChunkLoadEvent(), this);

        pm.registerEvents(new OnEntityDamageEvent(), this);
        pm.registerEvents(new OnEntityExplodeEvent(), this);
        pm.registerEvents(new OnEntitySpawnEvent(), this);
        pm.registerEvents(new OnEntityPickupItemEvent(), this);

        pm.registerEvents(new OnInventoryMoveEvent(), this);

        pm.registerEvents(new OnPlayerDeathEvent(), this);
        pm.registerEvents(new OnPlayerInteractAtEntityEvent(), this);
        pm.registerEvents(new OnPlayerInteractEvent(), this);
        pm.registerEvents(new OnPlayerInventoryCloseEvent(), this);
        pm.registerEvents(new OnPlayerJoinEvent(), this);
        pm.registerEvents(new OnPlayerMoveEvent(), this);
        pm.registerEvents(new OnPlayerQuitEvent(), this);
        pm.registerEvents(new OnPlayerGameModeChangeEvent(), this);
        pm.registerEvents(new OnPlayerDropItemEvent(), this);
        pm.registerEvents(new OnPlayerRespawnEvent(), this);

        // Loading
        gmiHandler = new GMIHandler();
        MySQLManager.enable();
        //log("loaded: "+Bukkit.getWorld("world").getUID().toString());
    }

    public void onDisable() {
        if (playerManager != null) playerManager.save();
        if (warpsManager != null) warpsManager.save();
        if (guildsManager != null) guildsManager.save();
        if (arenaManager != null) arenaManager.save();
        if (gameManager != null) gameManager.save();
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public HomesManager getHomesManager() {
        return homesManager;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }

    public GuildsManager getGuildsManager() {
        return guildsManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public void log(String update) {
        getLogger().log(Level.INFO, update);
    }

    public AuctionsManager getAuctionsManager() {
        return auctionsManager;
    }

    public LocksManager getLocksManager() {
        return locksManager;
    }

    public ShopsManager getShopsManager() {
        return shopsManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public WarpsManager getWarpsManager() {
        return warpsManager;
    }

    public GMIHandler getInventoryHandler() {
        return gmiHandler;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public void logToFile(String message) {
        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();
        File saveTo = new File(getDataFolder(), "data.txt");
        if (!saveTo.exists()) {
            try {
                saveTo.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            FileWriter fw = new FileWriter(saveTo,true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

