package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Commands.CurrencySetCommand;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class CurrencyCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public CurrencyCommand() {
        subCommands.add(new CurrencySetCommand());
        subCommands.add(new CurrencyAddCommand());
        subCommands.add(new CurrencySubCommand());
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
        return Plugin.currency;
    }

    @Override
    public String getPermission() {
        return "currency";
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
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

}
