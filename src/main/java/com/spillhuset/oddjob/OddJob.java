package com.spillhuset.oddjob;

import com.spillhuset.oddjob.Commands.Homes.HomesCommand;
import com.spillhuset.oddjob.Managers.HomesManager;
import com.spillhuset.oddjob.Managers.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class OddJob extends JavaPlugin {
    private static OddJob instance;
    private HomesManager homesManager;
    private PlayerManager playerManager;

    public static OddJob getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        playerManager = new PlayerManager();
        homesManager = new HomesManager();

        getCommand("homes").setExecutor(new HomesCommand());
    }

    public void onDisable() {

    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    public HomesManager getHomesManager() {
        return homesManager;
    }
}

