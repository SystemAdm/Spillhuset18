package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.Arena;
import com.spillhuset.oddjob.Utils.Game;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ArenaManager {
    public HashMap<String, Arena> arena = new HashMap<>();
    int unique = 0;
    HashMap<Integer, Game> games = new HashMap<>();

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
                arena.put(name, new Arena(name, world));
            }
        }
    }

    public void save() {
        for (String name : arena.keySet()) {
            save(name);
        }
    }

    public void save(String name) {
        Arena arena = this.arena.get(name);
        OddJob.getInstance().getConfig().set("arena." + name + ".world", arena.getWorld().getUID().toString());
    }

    public void save(Player player) {
        player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        save();
    }

    public void start(Arena arena) {
        WorldCreator worldCreator = new WorldCreator(unique+"_" +arena.getName());
        worldCreator.copy(arena.getWorld());
        World world = Bukkit.createWorld(worldCreator);
        this.unique++;
        games.put(unique,new Game(world,arena.getName()));
    }

    public void stop(int game_unique) {
        if (games.containsKey(game_unique)) {
            Game game = games.get(game_unique);
            game.stop();
            game.getWorld().getWorldFolder().delete();
        }
    }



}
