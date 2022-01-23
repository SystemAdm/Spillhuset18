package com.spillhuset.oddjob;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.spillhuset.oddjob.Commands.*;
import com.spillhuset.oddjob.Enums.Account;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Events.*;
import com.spillhuset.oddjob.Managers.*;
import com.spillhuset.oddjob.SQL.CurrencySQL;
import com.spillhuset.oddjob.Utils.Tool;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class OddJob extends JavaPlugin {

    private static OddJob instance;
    public List<Location> entrances;
    private HomesManager homesManager;
    private PlayerManager playerManager;
    private HistoryManager historyManager;
    private GuildsManager guildsManager;
    private TeleportManager teleportManager;
    private CurrencyManager currencyManager;
    private WarpManager warpManager;
    public HashMap<UUID, Double> earnings;
    private LocksManager locksManager;
    public DynmapAPI dynmap = null;
    public MarkerSet markerSet = null;
    public BossBar bossbar;
    private ShopsManager shopsManager;
    private HashMap<UUID,BossBar> bars = new HashMap<>();
    private AuctionsManager auctionsManager;

    public static OddJob getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("Dynmap");
        if (dynmap != null) {
            markerSet = dynmap.getMarkerAPI().createMarkerSet("spillhuset.guilds", "Guilds", null, false);
        }


        /* Loadings */
        ConfigManager.load();
        homesManager = new HomesManager();
        playerManager = new PlayerManager();
        guildsManager = new GuildsManager();
        guildsManager.loadStart();
        historyManager = new HistoryManager();
        teleportManager = new TeleportManager();
        currencyManager = new CurrencyManager();
        warpManager = new WarpManager();
        locksManager = new LocksManager();
        shopsManager = new ShopsManager();
        auctionsManager = new AuctionsManager();

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
        pm.registerEvents(new OnPlayerInteractEvent(), this);
        pm.registerEvents(new OnBlockFromToEvent(), this);

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
        getCommand("load").setExecutor(new LoadCommand());
        getCommand("currency").setExecutor(new CurrencyCommand());
        getCommand("warps").setExecutor(new WarpCommand());
        getCommand("players").setExecutor(new PlayersCommand());

        earnings = new HashMap<>();
        entrances = new ArrayList<>();
/*
        bossbar = Bukkit.createBossBar("Next payment in:", BarColor.BLUE, BarStyle.SOLID);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OddJob.getInstance(), () -> {
            long time = Bukkit.getServer().getWorlds().get(0).getTime() +12000;
            if (time >= 24000)time -= 24000;
            bossbar.setProgress(((double) time / 24000));
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                if (earnings.containsKey(uuid) && !bossbar.getPlayers().contains(player)) {
                    bossbar.addPlayer(player);
                }
                if (time >= 12000 && time <12021) {
                    double value = Tool.round(earnings.get(uuid));
                    CurrencySQL.add(uuid, value, Account.bank);
                    MessageManager.currency_auto(player, value);
                    earnings.remove(uuid);
                    bossbar.removePlayer(player);
                }
            }
        }, 20L, 20L);*/
    }

    @Override
    public void onDisable() {
        if (!earnings.isEmpty()) {
            for (UUID uuid : earnings.keySet()) {
                double value = Tool.round(earnings.get(uuid));
                CurrencySQL.add(uuid, value, Account.bank);
            }
        }

        OddJob.getInstance().getGuildsManager().saveChunks();
        OddJob.getInstance().getGuildsManager().saveMembersRoles();
        OddJob.getInstance().getGuildsManager().saveGuilds();
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

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public WorldEditPlugin getWorldEdit() {
        org.bukkit.plugin.Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (plugin instanceof WorldEditPlugin) {
            return (WorldEditPlugin) plugin;
        } else return null;
    }


    public LocksManager getLocksManager() {
        return locksManager;
    }

    public ShopsManager getShopsManager() {
        return shopsManager;
    }

    public AuctionsManager getAuctionsManager() {
        return auctionsManager;
    }

}

