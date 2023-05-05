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

import java.util.ArrayList;
import java.util.List;

public class InvseeCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    @Override
    public boolean denyConsole() {
        return true;
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!can(sender, true, true)) {
            return true;
        }
        if (!argsLength(sender, args.length)) {
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            MessageManager.errors_find_player(getPlugin(), args[0], sender);
            return true;
        }

        Player player = (Player) sender;
        player.openInventory(target.getInventory());
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
