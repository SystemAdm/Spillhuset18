package com.spillhuset.oddjob.Commands.Essentials;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HealCommand extends SubCommandInterface implements CommandExecutor {
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
        return "heal";
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
        if (!can(sender, false, true)) {
            return true;
        }
        if (args.length == 0) {
            OddJob.getInstance().getPlayerManager().healOne(sender);
        } else if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            OddJob.getInstance().getPlayerManager().healAll(sender);
        } else if (args.length == 1) {
            OddJob.getInstance().getPlayerManager().healOne(args[0], sender);
        } else {
            OddJob.getInstance().getPlayerManager().healMany(args, sender);
        }
        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }

        return list;
    }
}
