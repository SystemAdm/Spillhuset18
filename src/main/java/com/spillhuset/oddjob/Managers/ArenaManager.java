package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.Utils.Arena;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.util.HashMap;

public class ArenaManager {
    public HashMap<String, Arena> arena = new HashMap<>();

    public void createArena(String name, WorldType type, World.Environment environment) {
        WorldCreator worldCreator =new WorldCreator(name);
        worldCreator.environment(environment);
        worldCreator.type(type);
        Bukkit.createWorld(worldCreator);
    }
}
