package com.spillhuset.oddjob.Enums;

public enum Plu {
    TELEPORT_REQUEST(50, 1),
    DEFAULT(1, 1),
    GUILDS_CLAIMS(1000, 0.1),
    GUILDS_HOMES(5000, 0.5),
    GUILDS_HOMES_TELEPORT(50,1),
    GUILDS_OUTPOST(200000,1),
    // 1000 (n^2 + 5)
    PLAYER_HOMES(1000,0.5),
    PLAYER_HOMES_TELEPORT(50, 1);
    // 250 (n + 3)

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
