package com.spillhuset.oddjob.Utils;

import org.bukkit.World;

import java.util.UUID;

public class Cords {
    int x;
    int z;
    UUID world;
    UUID guild;

    public Cords(int x, int z, UUID world,UUID guild) {
        this.x = x;
        this.z = z;
        this.world = world;
        this.guild = guild;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public UUID getWorld() {
        return world;
    }

    public UUID getGuild() {
        return guild;
    }

    public void setGuild(UUID guild) {
        this.guild = guild;
    }
}
