package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GuildsSetCommand extends SubCommand {

    public GuildsSetCommand() {
        subCommands.add(new GuildsSetOpenCommand());
        subCommands.add(new GuildsSetRenameCommand());
        subCommands.add(new GuildsSetAreaCommand());
        subCommands.add(new GuildsSetFlowCommand());
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
        OddJob.getInstance().log("set");
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }
        finder(sender, args);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        //guilds set
        // List commands
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length > depth()) {
                if (name.equalsIgnoreCase(args[depth()])) {
                    return subCommand.getTabCompleter(sender, args);
                }
                if (args[depth()].isEmpty() || name.startsWith(args[depth()])) {
                    list.add(name);
                }
            }
        }

        return list;
    }

}
