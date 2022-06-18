package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ArenaSetCommand extends SubCommand {
    public ArenaSetCommand() {
        subCommands.add(new ArenaSetGameTypeCommand());
        /*subCommands.add(new ArenaSetMaxTeamsCommand());
        subCommands.add(new ArenaSetMinTeamsCommand());*/
        subCommands.add(new ArenaSetPPTCommand());
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
        return null;
    }

    @Override
    public String getName() {
        return null;
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
        return null;
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

    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        for (SubCommand subCommand : subCommands) {
            // Can use 'self' command
            if (can(sender, false, false)) {
                if (subCommand.getName().equalsIgnoreCase(args[depth()])) {
                    return subCommand.getTabCompleter(sender, args);
                } else if (args[depth()].isEmpty() || subCommand.getName().startsWith(args[depth()])) {
                    list.add(subCommand.getName());
                }
            }
        }
        return list;
    }
}
