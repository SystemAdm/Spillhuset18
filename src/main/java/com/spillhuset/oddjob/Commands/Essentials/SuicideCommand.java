package com.spillhuset.oddjob.Commands.Essentials;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SuicideCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {

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
        return Plugin.essentials;
    }

    @Override
    public String getPermission() {
        return "admin";
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
        if (!can(sender,false,true)) {
            return true;
        }
        if(!argsLength(sender,args.length)) {
            return true;
        }

        if (args.length == 0 && sender instanceof Player player) {
            player.setHealth(0);
            return true;
        } else if (!can(sender,true,true)) {
            return true;
        }

        List<String> affected = new ArrayList<>();
        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("admin") && !player.isOp()) {
                    player.setHealth(0);
                    affected.add(player.getName());
                }
            }
        } else if (args.length == 1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                MessageManager.errors_find_player(getPlugin(),args[0],sender);
                return true;
            }affected.add(player.getName());
        } else {
            for (String arg : args) {
                Player player = Bukkit.getPlayer(arg);
                if (player != null) {
                    player.setHealth(0);
                    affected.add(player.getName());
                }
            }
        }
        MessageManager.essentials_suicide(getPlugin(),affected,sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("admin") && !player.isOp()) {
                if (args[args.length-1].isEmpty() || player.getName().toLowerCase().startsWith(args[args.length-1].toLowerCase())) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
