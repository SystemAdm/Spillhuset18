package com.spillhuset.spawn.spillhusetspawn;

import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            World world = Bukkit.getWorld("world");
            Player target = player;

            if (args.length != 0) {
                for (int i = 0; i < args.length; i++) {
                    Bukkit.getLogger().info("count "+i);
                    for (World w : Bukkit.getWorlds()) {
                        if (w.getName().equalsIgnoreCase(args[i])) {
                            world = w;
                            break;
                        }
                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().equalsIgnoreCase(args[i])) {
                            target = p;
                        }
                    }
                }
            }

            target.teleport(world.getSpawnLocation());
        }
        return true;
    }
}
