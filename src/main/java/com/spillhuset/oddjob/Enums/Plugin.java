package com.spillhuset.oddjob.Enums;

import org.bukkit.ChatColor;

public enum Plugin {
    homes(ChatColor.YELLOW, "H"),
    guilds(ChatColor.BLUE, "G"),
    teleports(ChatColor.LIGHT_PURPLE, "T"),
    auctions(ChatColor.DARK_PURPLE, "A"),
    currency(ChatColor.GOLD, "C"),
    deaths(ChatColor.DARK_GRAY, "D"),

    join(ChatColor.GREEN, "+"),
    leave(ChatColor.RED, "-"),
    locks(ChatColor.YELLOW, "L"),
    players(ChatColor.DARK_GREEN, "P"),
    warps(ChatColor.DARK_BLUE, "W"),
    shops(ChatColor.YELLOW, "S"),
    essentials(ChatColor.BLUE, "E"),
    arena(ChatColor.RED, "A"),
    world(ChatColor.BLACK, "W"),
    plugin(ChatColor.GOLD, "P");

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

    public String getName() {
        return name();
    }
}
