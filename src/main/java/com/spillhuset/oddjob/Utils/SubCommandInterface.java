package com.spillhuset.oddjob.Utils;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommandInterface {
    public abstract boolean denyConsole();

    public abstract boolean denyOp();

    public abstract boolean canOthers();

    public abstract Plugin getPlugin();

    public abstract String getPermission();

    public abstract int minArgs();

    public abstract int maxArgs();

    public abstract int depth();

    public ArrayList<SubCommand> subCommands = new ArrayList<>();

    public abstract boolean onCommand(CommandSender sender, Command command, String label, String[] args);

    public abstract List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args);

    /**
     * Checking permission
     *
     * @param sender
     * @param others
     * @param response
     * @return
     */
    public boolean can(CommandSender sender, boolean others, boolean response) {
        if (denyConsole() && !(sender instanceof Player)) {
            if (response) MessageManager.errors_denied_console(getPlugin(), sender);
            return false;
        }

        if (denyOp() && sender.isOp()) {
            if (response) MessageManager.errors_denied_op(getPlugin(), sender);
            return false;
        }

        if (!sender.hasPermission(getPermission()) && !sender.isOp() && sender instanceof Player) {
            if (response) MessageManager.errors_denied_players(getPlugin(), sender);
            return false;
        }

        return true;
    }

    /**
     * Checking length of arguments, too many or too few
     *
     * @param sender    CommandSender
     * @param argsCount Integer
     * @return Boolean
     */
    public boolean argsLength(CommandSender sender, int argsCount) {
        if (maxArgs() != 0 && argsCount > maxArgs()) {
            MessageManager.errors_too_many_args(getPlugin(), sender);
            return false;
        } else if (argsCount < minArgs()) {
            MessageManager.errors_too_few_args(getPlugin(), sender);
            return false;
        }

        return true;
    }

    public void redirect(CommandSender sender, Plugin plugin, String subcommand) {
        Bukkit.dispatchCommand(sender, plugin.name() + " " + subcommand);
    }

    public void finder(CommandSender sender, String[] args) {
        if (args.length >= depth()) {
            OddJob.getInstance().log("args: " + args.length + " depth: " + depth());
            for (SubCommand subCommand : subCommands) {
                OddJob.getInstance().log("command: " + subCommand.getName());
                OddJob.getInstance().log("like: " + args[depth()]);
                if (subCommand.getName().equalsIgnoreCase(args[depth()])) {
                    if (subCommand.can(sender, false, true)) {
                        subCommand.getCommandExecutor(sender, args);
                        return;
                    }
                }
            }
        }
        MessageManager.sendSyntax(getPlugin(), list(sender), sender);
    }

    public List<String> tabs(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            if (can(sender, false, false)) {
                if (subCommand.hasGuild(sender, false)) {
                    if (subCommand.getName().equalsIgnoreCase(args[depth()])) {
                        return subCommand.getTabCompleter(sender, args);
                    } else if ((args[depth()].isEmpty() || subCommand.getName().startsWith(args[depth()]))) {
                        list.add(subCommand.getName());
                    }
                }
            }
        }
        return list;
    }

    public String list(CommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            if (subCommand.can(sender, false, false)) {
                stringBuilder.append(ChatColor.GRAY).append(subCommand.getName()).append(ChatColor.RESET).append(",");
            }
        }
        if (!stringBuilder.isEmpty()) stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        return stringBuilder.toString();
    }
}
