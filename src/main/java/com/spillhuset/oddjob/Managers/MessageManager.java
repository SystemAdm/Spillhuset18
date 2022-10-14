package com.spillhuset.oddjob.Managers;

import org.bukkit.command.CommandSender;

public class MessageManager {

    public static void homes_info(CommandSender sender, int bought, int max, double price) {
        sender.sendMessage("You have "+bought+" homes of "+max+", next home will cost "+price);
    }
}
