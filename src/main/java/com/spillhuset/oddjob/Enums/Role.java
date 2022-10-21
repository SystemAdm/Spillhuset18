package com.spillhuset.oddjob.Enums;

public enum Role {
    Master(9), Guest(1), Members(3), Mods(7);

    private final int level;

    Role(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
