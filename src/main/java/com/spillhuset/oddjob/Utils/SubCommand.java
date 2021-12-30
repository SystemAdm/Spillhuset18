package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class SubCommand {
    public List<SubCommand> subCommands = new ArrayList<>();
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
        OddJob.getInstance().log("player "+((sender instanceof Player)?1:0));
        OddJob.getInstance().log("permission "+((sender.hasPermission(getPermission()))?1:0));
        OddJob.getInstance().log("player "+((sender.isOp())?1:0));
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

    /**
     * @param sender CommandSender
     * @param args   Strings
     * @param errors Boolean - Stop after no subcommand found?
     * @return Boolean
     */
    public boolean subCommand(@Nonnull CommandSender sender, @Nonnull String[] args, boolean errors) {
        // Checking /homes <subcommand>
        StringBuilder sub = builder(sender, args);
        // set != null && args.length == 1
        //       false  &&  true  =  false

        // tp  == null && args.length == 1
        //       true   &&  true  =  true
        boolean subcommand = sub == null && args.length != 0;
        if (args.length == 0 && sub != null) {
            if (!(sender instanceof Player)) {
                if (sub.isEmpty()) {
                    if (errors) {
                        MessageManager.errors_no_subcommands(getPlugin(), sender);
                        return false;
                    }
                } else {
                    if (errors) {
                        MessageManager.sendSyntax(getPlugin(), sub.toString(), sender);
                        return false;
                    }
                }
                return false;
            }
            redirect(sender, getPlugin(), "list");
            return true;
        }
        return subcommand;
    }
    public void redirect(CommandSender sender, Plugin plugin, String subcommand) {
        Bukkit.dispatchCommand(sender, plugin.name() + " " + subcommand);
    }

    /**
     * Listing subcommands as a comma separated String,
     * will return NULL if a subcommand is triggered,
     * returns an empty String if no subcommands exists
     *
     * @param sender CommandSender
     * @param args   Strings
     * @return NULL or String
     */
    public StringBuilder builder(CommandSender sender, String[] args) {
        StringBuilder sub = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (subCommand.can(sender, false, false)) {
                if (args.length >= 1 && name.equalsIgnoreCase(args[0]) && !args[0].equals("help")) {
                    subCommand.getCommandExecutor(sender, args);
                    // found a subcommand return 'null'
                    return null;
                }
                sub.append(ChatColor.GRAY).append(name).append(ChatColor.RESET).append(",");
            }
        }
        if (!sub.isEmpty()) sub.deleteCharAt(sub.lastIndexOf(","));
        // not found subcommand, sub will be an empty string
        return sub;
    }
}
