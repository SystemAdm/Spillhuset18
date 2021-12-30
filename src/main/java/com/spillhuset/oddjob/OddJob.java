package com.spillhuset.oddjob;

import com.spillhuset.oddjob.Commands.*;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Events.*;
import com.spillhuset.oddjob.Managers.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Currency;


public class OddJob extends JavaPlugin {

    private static OddJob instance;
    private HomesManager homesManager;
    private PlayerManager playerManager;
    private HistoryManager historyManager;
    private GuildsManager guildsManager;
    private TeleportManager teleportManager;
    private CurrencyManager currencyManager;

    public static OddJob getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        /* Loadings */
        ConfigManager.load();
        homesManager = new HomesManager();
        playerManager = new PlayerManager();
        guildsManager = new GuildsManager();
        guildsManager.loadStart();
        historyManager = new HistoryManager();
        teleportManager = new TeleportManager();
        currencyManager = new CurrencyManager();

        /* Listeners */
        pm.registerEvents(new OnPlayerJoinEvent(), this);
        pm.registerEvents(new OnPlayerQuitEvent(), this);
        pm.registerEvents(new OnPlayerMoveEvent(), this);
        pm.registerEvents(new OnEntityDamageEvent(), this);
        pm.registerEvents(new OnBlockPlaceEvent(), this);
        pm.registerEvents(new OnBlockBreakEvent(), this);
        pm.registerEvents(new OnPlayerBucketEmptyEvent(), this);
        pm.registerEvents(new OnEntityExplodeEvent(), this);
        pm.registerEvents(new OnChunkLoadEvent(), this);
        pm.registerEvents(new OnPlayerDeathEvent(), this);
        pm.registerEvents(new OnPlayerInteractAtEntityEvent(), this);
        pm.registerEvents(new OnPlayerInventoryCloseEvent(), this);

        /* Commands */
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("guilds").setExecutor(new GuildsCommand());
        getCommand("suicide").setExecutor(new SuicideCommand());
        getCommand("teleport").setExecutor(new TeleportCommand());
        getCommand("locks").setExecutor(new LocksCommand());
        getCommand("invsee").setExecutor(new InvseeCommand());
        getCommand("enderchest").setExecutor(new EnderchestCommand());
        getCommand("auctions").setExecutor(new AuctionsCommand());
        getCommand("shops").setExecutor(new ShopsCommand());

    }

    @Override
    public void onDisable() {

    }

    public HomesManager getHomeManager() {
        return homesManager;
    }

    public void debug(Plugin plugin, String context, String message) {
        if (getConfig().getBoolean("debug")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + plugin.name() + " " + ChatColor.RESET + context + " " + ChatColor.YELLOW + message);
        }
    }

    public void log(String string) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + string);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public GuildsManager getGuildsManager() {
        return guildsManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }


}

