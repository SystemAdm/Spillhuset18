package com.spillhuset.oddjob.Enums;

public enum GuildType {
    player("uuid"), uuid("player");

    final String type;

    GuildType(String type) {
        this.type = type;

    }

    public String type() {
        return type;
    }
}
