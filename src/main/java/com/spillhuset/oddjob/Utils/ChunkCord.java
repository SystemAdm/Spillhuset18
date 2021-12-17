package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.OddJob;

import java.util.UUID;

public class ChunkCord {
    private UUID guild;
    private UUID world;
    private int x;
    private int z;

    public ChunkCord(UUID world, int x, int z, UUID guild) {
        OddJob.getInstance().log("created a fuck");
        this.world = world;
        this.x = x;
        this.z = z;
        this.guild = guild;
    }

    public int getX() {
        return x;
    }

    public UUID getWorld() {
        return world;
    }

    public int getZ() {
        return z;
    }

    public UUID getGuild() {
        return guild;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setGuild(UUID guild) {
        this.guild = guild;
    }

    public void setWorld(UUID world) {
        this.world = world;
    }
}
