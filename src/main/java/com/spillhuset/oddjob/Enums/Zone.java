package com.spillhuset.oddjob.Enums;

import org.bukkit.ChatColor;

public enum Zone {
    SAFE(ChatColor.DARK_GREEN, "00FF00", "Better safe than sorry, no worry!"),
    SHOP(ChatColor.GOLD,"00FF00","Sell it!"),
    AUCTION(ChatColor.GOLD,"00FF00","Money is business!"),
    BANK(ChatColor.GOLD,"00FF00","Money is life."),
    WILD(ChatColor.YELLOW, "00FFFF", "Welcome to the WILD!"),
    GUILD(ChatColor.DARK_BLUE, "0000FF", "Here lives "),
    WAR(ChatColor.DARK_RED, "FF0000", "This means WAR!"),
    ARENA(ChatColor.RED, "FF0000", "Game on!"),
    JAIL(ChatColor.YELLOW, "00FFFF", "Take your time.");

    private final ChatColor color;
    private final String string;
    private final String code;

    Zone(ChatColor color, String code, String string) {
        this.color = color;
        this.string = string;
        this.code = code;
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
        return getColor() + getString() + ChatColor.BLUE+name;
    }
    public String getColorCode() {
        return code;
    }
}
