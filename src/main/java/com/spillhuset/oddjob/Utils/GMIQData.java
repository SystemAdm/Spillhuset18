package com.spillhuset.oddjob.Utils;

public class GMIQData {
    private final String chunk;
    private final String location;

    public GMIQData(String chunk,String location) {
        this.chunk = chunk;
        this.location = location;
    }

    public String getChunk() {
        return chunk;
    }

    public String getLocation() {
        return location;
    }
}
