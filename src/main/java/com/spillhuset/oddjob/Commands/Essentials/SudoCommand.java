package com.spillhuset.oddjob.Commands.Essentials;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SudoCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
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
        return "essentials.sudo";
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
        if (!argsLength(sender, args.length)) {
            return true;
        }
        if (!can(sender, false, true)) {
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            MessageManager.errors_find_player(getPlugin(), args[0], sender);
            return true;
        }

        StringBuilder cmd = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            cmd.append(args[i]).append(" ");
        }

        Bukkit.dispatchCommand(player, cmd.toString());
        sender.sendMessage(player.getName() + " did '" + cmd + "'");

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> list = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp() || !player.hasPermission("admin")) {
                if (args[0].isEmpty() || player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(player.getName());
                }
            }
        }
        return list;
    }
}
