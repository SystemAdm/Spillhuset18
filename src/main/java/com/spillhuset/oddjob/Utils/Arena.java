package com.spillhuset.oddjob.Utils;

import org.bukkit.World;

import java.util.UUID;

public class Arena {
    String name;
    UUID world;
    UUID uuid;

    public Arena(UUID uuid, String name, UUID world) {
        this.uuid = uuid;
        this.name = name;
        this.world = world;
    }

    public String getName() {
        return this.name;
    }

    public UUID getWorld() {
        return this.world;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}
