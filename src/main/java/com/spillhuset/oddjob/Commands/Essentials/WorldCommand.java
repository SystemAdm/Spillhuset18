package com.spillhuset.oddjob.Commands.Essentials;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WorldCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean denyConsole() {
        return false;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public boolean canOthers() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.world;
    }

    @Override
    public String getPermission() {
        return "world";
    }

    @Override
    public int minArgs() {
        return 0;
    }

    @Override
    public int maxArgs() {
        return 0;
    }

    @Override
    public int depth() {
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!can(sender, false, true)) return true;
        if (!argsLength(sender, args.length)) return true;

        if (sender instanceof Player player) {
            List<World> worlds = Bukkit.getWorlds();
            for (World world : worlds) {
                if (world.getName().equalsIgnoreCase(args[0])) {
                    player.teleport(world.getSpawnLocation());
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            if (args[0].isEmpty() || world.getName().startsWith(args[0])) {
                list.add(world.getName());
            }
        }
        return list;
    }
}
