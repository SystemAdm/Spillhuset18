package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SubCommand {
    public abstract boolean denyConsole();

    public abstract boolean denyOp();

    public abstract Plugin getPlugin();

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

    public abstract String getPermission();

    public abstract int minArgs();

    public abstract int maxArgs();

    public abstract void getCommandExecutor(CommandSender sender, String[] args);

    public abstract List<String> getTabCompleter(CommandSender sender, String[] args);

    public boolean can(CommandSender sender, boolean others, boolean response) {
        if (denyConsole() && !(sender instanceof Player)) {
            if (response) MessageManager.errors_denied_console(getPlugin(), sender);
            return false;
        }
        if (denyOp() && sender.isOp()) {
            if (response) MessageManager.errors_denied_op(getPlugin(), sender);
            return false;
        }
        if (sender instanceof Player && others && !sender.hasPermission(getPermission()+".others")) {
            if (response) MessageManager.errors_denied_others(getPlugin(), sender);
            return false;
        }
        if (sender instanceof Player && (!sender.hasPermission(getPermission()) && !sender.isOp())) {
            if (response) MessageManager.errors_denied_players(getPlugin(), sender);
            return false;
        }
        return true;
    }

    /**
     * Check the number of arguments, if it is within the range, returns true.
     *
     * @param sender     CommandSender
     * @param argsLength Integer; length of args
     * @return Boolean
     */
    public boolean argsLength(CommandSender sender, int argsLength) {
        if (maxArgs() != 0 && argsLength > maxArgs()) {
            MessageManager.errors_too_many_args(getPlugin(), sender);
            return false;
        }
        if (argsLength < minArgs()) {
            MessageManager.errors_too_few_args(getPlugin(), sender);
            return false;
        }
        return true;
    }
}
