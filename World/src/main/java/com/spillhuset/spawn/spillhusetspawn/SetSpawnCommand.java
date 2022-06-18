package com.spillhuset.spawn.spillhusetspawn;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && player.isOp()) {
            World world = player.getWorld();
            world.setSpawnLocation(player.getLocation());
            sender.sendMessage(ChatColor.GREEN+"New spawn location set!");
        }
        return true;
    }
}
