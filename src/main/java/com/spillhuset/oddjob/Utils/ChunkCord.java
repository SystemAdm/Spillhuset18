package com.spillhuset.oddjob.Utils;

import java.util.UUID;

public class ChunkCord {
    public UUID getVisitingGuild() {
        return visitingGuild;
    }

    private final UUID visitingGuild;
    private final UUID world;
    private final int x;

    public UUID getWorldUuid() {
        return world;
    }

    public int getZ() {
        return z;
    }

    private final int z;

    public ChunkCord(UUID world, int x, int z, UUID visitingGuild) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.visitingGuild = visitingGuild;
    }

    public int getX() {
        return x;
    }
}
