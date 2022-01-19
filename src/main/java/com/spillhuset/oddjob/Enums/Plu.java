package com.spillhuset.oddjob.Enums;

public enum Plu {
    HOMES_SET(200, 1),
    TELEPORT_REQUEST(50, 1),
    DEFAULT(0.10, 1),
    GUILDS_CLAIMS(1000, 0.1),
    GUILDS_HOMES(5000, 0.5),
    PLAYER_HOMES(1000,0.5);

    public final double value;
    public final double multiplier;

    Plu(double value, double multiplier) {
        this.value = value;
        this.multiplier = multiplier;
    }

    public double getValue() {
        return value;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
