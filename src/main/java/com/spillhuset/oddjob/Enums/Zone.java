package com.spillhuset.oddjob.Enums;

import org.bukkit.ChatColor;

public enum Zone {
    SAFE(ChatColor.DARK_GREEN, "Better safe than sorry, no worry!"),
    WILD(ChatColor.YELLOW, "Welcome to the WILD!"),
    GUILD(ChatColor.DARK_BLUE, "Here lives "),
    WAR(ChatColor.DARK_RED, "This means WAR!"),
    ARENA(ChatColor.RED, "Game on!"),
    JAIL(ChatColor.YELLOW, "Take your time.");

    private final ChatColor color;
    private final String string;

    Zone(ChatColor color, String string) {
        this.color = color;
        this.string = string;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getString() {
        return string;
    }

    public String getColoredString() {
        return getColor() + getString();
    }

    public String getColoredString(String name) {
        return getColor() + getString() + name;
    }
}
