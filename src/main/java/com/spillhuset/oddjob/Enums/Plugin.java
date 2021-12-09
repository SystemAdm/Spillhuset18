package com.spillhuset.oddjob.Enums;

import org.bukkit.ChatColor;

public enum Plugin {
    homes(ChatColor.YELLOW, "H"),
    guilds(ChatColor.BLUE, "G"),
    teleport(ChatColor.LIGHT_PURPLE, "T"),
    auctions(ChatColor.DARK_PURPLE, "A"),
    currency(ChatColor.GOLD, "C"),
    messages(ChatColor.GRAY, "M"),
    deaths(ChatColor.DARK_GRAY, "D"),

    join(ChatColor.GREEN, "+"),
    leave(ChatColor.RED, "-");

    private final String string;
    private final ChatColor color;

    Plugin(ChatColor color, String string) {
        this.string = string;
        this.color = color;
    }

    public String getShort() {
        return string;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getString() {
        return "[" + getColor() + getShort() + ChatColor.RESET + "] ";
    }
}
