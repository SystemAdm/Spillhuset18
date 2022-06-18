package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Arena;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.HashMap;

public class ArenaManager {
    public HashMap<String, Arena> arena = new HashMap<>();

    public void createArena(String name, WorldType type, World.Environment environment) {
        WorldCreator worldCreator = new WorldCreator(name);
        worldCreator.environment(environment);
        worldCreator.type(type);

        OddJob.getInstance().getConfig().set("arena." + name + ".name", name);
        OddJob.getInstance().saveConfig();
        Bukkit.createWorld(worldCreator);
    }

    public void loadArena() {
        if (OddJob.getInstance().getConfig().getConfigurationSection("arena") != null) {
            for (String name : OddJob.getInstance().getConfig().getConfigurationSection("arena").getKeys(false)) {
                WorldCreator creator = new WorldCreator(name);
                World world = Bukkit.createWorld(creator);
                arena.put(name,new Arena(name,world));
            }
        }
    }
}
