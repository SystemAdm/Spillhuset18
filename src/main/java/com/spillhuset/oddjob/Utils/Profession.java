package com.spillhuset.oddjob.Utils;

public enum Profession {
    LUMBERJACK("Jack, keep chopping that lumber.", "logs", "Cutting trees and planting trees"),
    MINER("Miners gonna mine", "ores", "Mining ores"),
    MONSTER_HUNTER("Kill that bastard", "resources", "Killing mobs"),
    FISHER("And you call that a fish?", "fish", "Fishing"),
    FARMER("Wheat it", "vegetable", "Farming"),
    BUTCHER("Mooooo", "meat", "Killing animals");

    private Profession(String description, String gain, String drop) {
        this.description = description;
        this.gain = gain;
        this.drop = drop;
    }

    private final String description;
    private final String gain;

    public String getGain(boolean string) {
        return string ? "get a 5% selling bonus at a vendor, when selling " + gain : gain;
    }

    public String getDrop(boolean string) {
        return string ? "get 50% extra xp when " + drop : drop;
    }

    private final String drop;

    public String getDescription() {
        return description;
    }
}
