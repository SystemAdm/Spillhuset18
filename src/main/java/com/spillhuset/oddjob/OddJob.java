package com.spillhuset.oddjob;

import com.spillhuset.oddjob.Commands.GuildsCommand;
import com.spillhuset.oddjob.Commands.HomesCommand;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Events.OnJoinEvent;
import com.spillhuset.oddjob.Events.OnLeaveEvent;
import com.spillhuset.oddjob.Events.OnMoveEvent;
import com.spillhuset.oddjob.Managers.GuildsManager;
import com.spillhuset.oddjob.Managers.HistoryManager;
import com.spillhuset.oddjob.Managers.HomesManager;
import com.spillhuset.oddjob.Managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import static com.spillhuset.oddjob.Managers.ConfigManager.load;

public class OddJob extends JavaPlugin {

    private static OddJob instance;
    private HomesManager homesManager;
    private PlayerManager playerManager;
    private HistoryManager historyManager;
    private GuildsManager guildsManager;

    public static OddJob getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = getServer().getPluginManager();

        /* Loadings */
        load();
        homesManager = new HomesManager();
        playerManager = new PlayerManager();
        guildsManager = new GuildsManager();
        guildsManager.loadStart();
        historyManager = new HistoryManager();

        /* Listeners */
        pm.registerEvents(new OnJoinEvent(), this);
        pm.registerEvents(new OnLeaveEvent(), this);
        pm.registerEvents(new OnMoveEvent(),this);

        /* Commands */
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("guilds").setExecutor(new GuildsCommand());
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
}

