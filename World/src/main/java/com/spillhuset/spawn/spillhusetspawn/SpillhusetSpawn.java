package com.spillhuset.spawn.spillhusetspawn;

import org.bukkit.plugin.java.JavaPlugin;

public final class SpillhusetSpawn extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
