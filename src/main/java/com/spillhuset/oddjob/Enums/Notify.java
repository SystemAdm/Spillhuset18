package com.spillhuset.oddjob.Enums;

import org.bukkit.ChatColor;

public enum Notify {
    warning(ChatColor.YELLOW), danger(ChatColor.RED), success(ChatColor.GREEN), info(ChatColor.AQUA);

    private final ChatColor color;

    Notify(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
