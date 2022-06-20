package com.spillhuset.oddjob.Utils;

import org.bukkit.World;

public class Game {
    private final World world;
    private final String arenaName;

    public Game(World world, String arenaName) {
        this.world = world;
        this.arenaName = arenaName;
    }

    public World getWorld() {
        return world;
    }

    public void stop() {

    }

    public String getArenaName() {
        return arenaName;
    }
}
