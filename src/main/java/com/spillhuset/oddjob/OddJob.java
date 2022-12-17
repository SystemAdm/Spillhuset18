package com.spillhuset.oddjob;

import com.spillhuset.oddjob.Commands.Currency.CurrencyCommand;
import com.spillhuset.oddjob.Commands.Homes.HomesCommand;
import com.spillhuset.oddjob.Commands.Locks.LocksCommand;
import com.spillhuset.oddjob.Commands.Warps.WarpCommand;
import com.spillhuset.oddjob.Events.OnBlockBreakEvent;
import com.spillhuset.oddjob.Events.OnPlayerInteractEvent;
import com.spillhuset.oddjob.Managers.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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

    public static OddJob getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        ConfigManager.load();
        playerManager = new PlayerManager();
        if (ConfigManager.getBoolean("plugin.homes")) {
            homesManager = new HomesManager();
            getCommand("homes").setExecutor(new HomesCommand());
        }
        if (ConfigManager.getBoolean("plugin.currency")) {
            currencyManager = new CurrencyManager();
            getCommand("currency").setExecutor(new CurrencyCommand());
        }
        if (ConfigManager.getBoolean("plugin.guilds")) guildsManager = new GuildsManager();
        if (ConfigManager.getBoolean("plugin.arena")) arenaManager = new ArenaManager();
        if (ConfigManager.getBoolean("plugin.auctions")) auctionsManager = new AuctionsManager();
        if (ConfigManager.getBoolean("plugin.locks")) {
            locksManager = new LocksManager();
            getCommand("locks").setExecutor(new LocksCommand());
        }
        if (ConfigManager.getBoolean("plugin.shops")) shopsManager = new ShopsManager();
        if (ConfigManager.getBoolean("plugin.teleport")) teleportManager = new TeleportManager();
        if (ConfigManager.getBoolean("plugin.warps")) {
            warpsManager = new WarpsManager();
            getCommand("warps").setExecutor(new WarpCommand());
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new OnPlayerInteractEvent(), this);
        pm.registerEvents(new OnBlockBreakEvent(), this);
    }

    public void onDisable() {

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
}

