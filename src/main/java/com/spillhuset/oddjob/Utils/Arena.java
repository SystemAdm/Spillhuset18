package com.spillhuset.oddjob.Utils;

import org.bukkit.World;

import java.util.UUID;

public class Arena {
    String name;
    World world;
    UUID uuid;

    public Arena(String name, World world) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.world = world;
    }

    public String getName() {
        return this.name;
    }

    public World getWorld() {
        return this.world;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
