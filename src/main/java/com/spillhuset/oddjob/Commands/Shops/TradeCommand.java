package com.spillhuset.oddjob.Commands.Shops;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TradeCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public TradeCommand() {
        subCommands.add(new TradeRequestCommand());
        subCommands.add(new TradeAcceptCommand());
        subCommands.add(new TradeDenyCommand());
    }

    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return true;
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
        return "trade";
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
        if (!argsLength(sender, args.length)) {
            return true;
        }
        if (!can(sender, false, true)) {
            return true;
        }

        if (args.length > 0) {
            finder(sender, args);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return tabs(sender, args);
    }
}
