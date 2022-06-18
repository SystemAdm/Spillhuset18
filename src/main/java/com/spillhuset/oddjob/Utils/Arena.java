package com.spillhuset.oddjob.Utils;

import org.bukkit.World;

public class Arena {
    String name;
    World world;

    public Arena(String name, World world) {
        this.name = name;
        this.world = world;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }
}
