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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ShopsCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public ShopsCommand() {
        subCommands.add(new ShopsSellCommand());
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
        return Plugin.shops;
    }

    @Override
    public String getPermission() {
        return "shops";
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
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player player) {
                UUID guild = OddJob.getInstance().getGuildsManager().getMembers().get(player.getUniqueId());
                if (guild != null) {
                    MessageManager.sendSyntax(getPlugin(), builder(sender, args).toString(), sender);
                } else {
                    Bukkit.dispatchCommand(sender, "shops help");
                }
            } else {
                MessageManager.sendSyntax(getPlugin(), builder(sender, args).toString(), sender);
            }
            return true;
        }

        boolean sub = subCommand(sender, args, false);
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            args[0] = null;
            MessageManager.sendSyntax(getPlugin(), builder(sender, args).toString(), sender);
            return true;
        }
        OddJob.getInstance().log("sub:" + (sub ? "ok" : "n"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
