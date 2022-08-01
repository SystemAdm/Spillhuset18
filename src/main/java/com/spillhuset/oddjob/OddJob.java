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
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
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
    public MarkerSet markerGuilds = null;
    public MarkerSet markerToombs = null;
    public BossBar bossbar;
    private ShopsManager shopsManager;
    private ArenaManager arenaManager;
    private final HashMap<UUID, BossBar> bars = new HashMap<>();
    private AuctionsManager auctionsManager;
    int counter = 0;
    int time = 300;

    public static OddJob getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        dynmap = (DynmapAPI) Bukkit.getServer().getPluginManager().getPlugin("Dynmap");
        if (dynmap != null) {
            markerGuilds = dynmap.getMarkerAPI().createMarkerSet("spillhuset.guilds", "Guilds", null, false);
            markerToombs = dynmap.getMarkerAPI().createMarkerSet("spillhuset.toombs", "Toombs", null, false);
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
        arenaManager = new ArenaManager();

        /* Listeners */
        //pm.registerEvents(new StrongholdEvent(),this);
        pm.registerEvents(new OnPlayerInteractAtEntityEvent(), this);
        pm.registerEvents(new OnEntitySpawnEvent(), this);
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
        pm.registerEvents(new OnPlayerInventoryCloseEvent(), this);
        pm.registerEvents(new OnPlayerInteractEvent(), this);
        pm.registerEvents(new OnBlockFromToEvent(), this);
        pm.registerEvents(new OnPlayerDropItemEvent(), this);
        pm.registerEvents(new OnPlayerGameModeChangeEvent(), this);
        pm.registerEvents(new OnInventoryMoveEvent(), this);

        /* Commands */
        getCommand("arena").setExecutor(new ArenaCommand());
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
        getCommand("sudo").setExecutor(new SudoCommand());
        //getCommand("monster").setExecutor(new MonsterCommand());
        //getCommand("curse").setExecutor(new CurseCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("world").setExecutor(new WorldCommand());


        earnings = new HashMap<>();
        entrances = new ArrayList<>();

        arenaManager.loadArena();

        bossbar = Bukkit.createBossBar("", BarColor.BLUE, BarStyle.SOLID);
        new BukkitRunnable() {
            @Override
            public void run() {
                String minS = "";
                String secS = "";
                int min = 0;
                int sec = 0;
                if (counter > 59) {
                    min = (int) Math.floor(counter / 60d);
                    sec = counter - (min * 60);
                } else
                    sec = counter;

                if (min == 1) {
                    minS = min + "min ";
                } else if (min != 0) {
                    minS = min + "mins ";
                }

                if (sec == 1) {
                    secS = sec + "sec ";
                } else if (sec != 0) {
                    secS = sec + "secs ";
                }

                bossbar.setTitle("Next payment in: " + minS + secS);
                bossbar.setProgress((double) counter / (double) time);
                if (counter <= 0) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        UUID uuid = player.getUniqueId();
                        if (earnings.containsKey(uuid)) {
                            double value = Math.round(earnings.getOrDefault(uuid, 0d));
                            CurrencySQL.add(uuid, value, Account.bank);
                            MessageManager.currency_auto(player, value);
                            earnings.remove(uuid);
                            bossbar.removePlayer(player);

                        }
                    }

                }
                if (counter == 0) counter = time;
                counter--;
            }
        }.runTaskTimerAsynchronously(OddJob.getInstance(), 20, 20);
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

    public ArenaManager getArenaManager() {
        return arenaManager;
    }
}

