package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXNotSupportedException;

import java.util.ArrayList;
import java.util.List;

public class ShopsCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public ShopsCommand() {
        subCommands.add(new ShopsCreateCommand());
        subCommands.add(new ShopsListCommand());
        subCommands.add(new ShopsInfoCommand());
        subCommands.add(new ShopsSetCommand());
        subCommands.add(new ShopsSellCommand());
        subCommands.add(new ShopsPriceCommand());
        subCommands.add(new ShopsBuyCommand());
        subCommands.add(new ShopsAddItemCommand());
        subCommands.add(new ShopsSetItemCommand());
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

        if (args.length > 0) {
            finder(sender, args);
        }
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
