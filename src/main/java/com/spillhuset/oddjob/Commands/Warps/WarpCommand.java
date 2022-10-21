package com.spillhuset.oddjob.Commands.Warps;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WarpCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public WarpCommand() {
        subCommands.add(new WarpAddCommand());
        subCommands.add(new WarpDelCommand());
        subCommands.add(new WarpListCommand());
        subCommands.add(new WarpTeleportCommand());
        //subCommands.add(new WarpPortalCommand());
        subCommands.add(new WarpRenameCommand());
        subCommands.add(new WarpRelocateCommand());
        subCommands.add(new WarpCostCommand());
        subCommands.add(new WarpPasswdCommand());
        //subCommands.add(new WarpLinkCommand());
        //subCommands.add(new WarpUnlinkCommand());
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
        return Plugin.warps;
    }

    @Override
    public String getPermission() {
        return "warps";
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

        if (!argsLength(sender, args.length)) {
            return true;
        }
        finder(sender,args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
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
