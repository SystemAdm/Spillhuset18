package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
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

public class TeleportCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public TeleportCommand() {
        subCommands.add(new TeleportRequestCommand());
        subCommands.add(new TeleportAcceptCommand());
        subCommands.add(new TeleportDenyCommand());
    }

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
        return Plugin.teleport;
    }

    @Override
    public String getPermission() {
        return "teleports";
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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!argsLength(sender, args.length)) {
            return true;
        }
        if (!can(sender, true, true)) {
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player player) {
                Bukkit.dispatchCommand(sender, "teleport help");
            } else {
                MessageManager.sendSyntax(getPlugin(), builder(sender, args).toString(), sender);
            }
            return true;
        }

        boolean sub = subCommand(sender, args, false);
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            MessageManager.sendSyntax(getPlugin(), builder(sender, args).toString(), sender);
            return true;
        } else if (args.length == 1 && sender instanceof Player player) {
            OddJob.getInstance().getTeleportManager().teleport(player, args[0]);
            return true;
        } else if (args.length == 2) {
            OddJob.getInstance().getTeleportManager().teleport(sender, args[0], args[1]);
            return true;
        }
        OddJob.getInstance().log("sub:" + (sub ? "ok" : "n"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        OddJob.getInstance().log("here yet?");
        List<String> list = new ArrayList<>();

        // List commands
        for (SubCommand subCommand : subCommands) {
            if (subCommand.can(sender, false, false)) {
                if (args.length == 0 || (args.length == 1 && subCommand.getName().startsWith(args[0]))) {
                    list.add(subCommand.getName());
                } else if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    return subCommand.getTabCompleter(sender, args);
                }
            }
        }

        return list;
    }
}
