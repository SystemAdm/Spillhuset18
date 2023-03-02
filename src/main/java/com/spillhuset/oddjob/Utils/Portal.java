package com.spillhuset.oddjob.Utils;

import java.util.UUID;

public class Portal {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getWarp() {
        return warp;
    }

    public void setWarp(UUID warp) {
        this.warp = warp;
    }

    private UUID warp;

    public Portal(String name, UUID warpUUID) {
        this.name = name;
        this.warp = warpUUID;
    }

}
