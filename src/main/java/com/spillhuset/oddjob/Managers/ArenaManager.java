package com.spillhuset.oddjob.Managers;

import com.spillhuset.oddjob.SQL.ArenaSQL;
import com.spillhuset.oddjob.Utils.Arena;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class ArenaManager {
    private final HashMap<UUID, Arena> arenas = new HashMap<>();

    public void create(CommandSender sender, String name, World.Environment environment, WorldType type) {
        for (Arena arena : arenas.values()) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return;
            }
        }

        String arenaName = "arena_" + name;

        WorldCreator wc = new WorldCreator(arenaName);
        wc.environment(environment);
        wc.type(type);
        World world = wc.createWorld();
        if (world == null) {
            // Error world
            return;
        }
        UUID id = UUID.randomUUID();
        Arena arena = new Arena(id, name, world.getUID());
        Player player = (Player) sender;
        Location location = world.getHighestBlockAt(0, 0).getLocation();
        player.teleport(location);
        arenas.put(id, arena);
        ArenaSQL.save(arena);
    }

    public void delete(String name) {
        Arena arena = null;
        for (Arena a : arenas.values()) {
            if (a.getName().equalsIgnoreCase(name)) {
                arena = a;
            }
        }

        if (arena == null) {
            // Not existing
            return;
        }

        arenas.remove(arena.getUuid());
        ArenaSQL.delete(arena.getUuid());

        World world = Bukkit.getWorld(arena.getWorld());
        if (world != null) {
            Bukkit.unloadWorld(world,false);
            delete(world.getWorldFolder());
        }
    }

    public void delete(File file) {
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File value : files) {
                    if (value.isDirectory()) {
                        delete(value);
                    } else {
                        value.delete();
                    }
                }
            }
        }
        file.delete();
    }

    public void save() {
    }
}
