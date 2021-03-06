package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GuildsSetCommand extends SubCommand {
    private final List<SubCommand> subCommands = new ArrayList<>();
    public GuildsSetCommand() {
        subCommands.add(new GuildsSetOpenCommand());
        subCommands.add(new GuildsSetRenameCommand());
        subCommands.add(new GuildsSetAreaCommand());
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
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "guilds";
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
        return 1;
    }
    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return false;
    }

    @Override
    public Role guildRole() {
        return null;
    }
    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        StringBuilder sub = new StringBuilder();

        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (subCommand.can(sender,false,true)) {
                if (args.length >=1 && subCommand.getName().startsWith(args[1])) {
                    subCommand.getCommandExecutor(sender,args);
                    return;
                } else {
                    sub.append(ChatColor.GRAY).append(name).append(ChatColor.RESET).append(",");
                }
            }
        }
        if (!sub.isEmpty()) {
            sub.deleteCharAt(sub.lastIndexOf(","));
            MessageManager.sendSyntax(Plugin.guilds,sub.toString(),sender);
        }
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        // List commands
        for (SubCommand subCommand : subCommands) {
            if (subCommand.can(sender, false, false)) {
                if (args.length == 1 || (args.length == 2 && subCommand.getName().startsWith(args[1]))) {
                    list.add(subCommand.getName());
                } else if (subCommand.getName().equalsIgnoreCase(args[1])) {
                    return subCommand.getTabCompleter(sender, args);
                }
            }
        }

        return list;
    }

}
