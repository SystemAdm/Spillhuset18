package com.spillhuset.oddjob.Enums;

public enum CountdownSpeed {
    normal(20), fast(10), instant(0), warp(5);

    private long timer;

    CountdownSpeed(long timer) {
        this.timer = timer;
    }

    public long getTimer() {
        return timer;
    }
}
