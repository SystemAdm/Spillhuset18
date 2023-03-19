package com.spillhuset.oddjob.Commands.Guilds;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GuildsSetFlowCommand extends SubCommand {

    public GuildsSetFlowCommand() {
        subCommands.add(new GuildsSetFlowWaterCommand());
        subCommands.add(new GuildsSetFlowLavaCommand());
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
    public Plugin getPlugin() {
        return Plugin.guilds;
    }

    @Override
    public String getName() {
        return "flow";
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
        return 2;
    }

    @Override
    public boolean noGuild() {
        return false;
    }

    @Override
    public boolean needGuild() {
        return true;
    }

    @Override
    public Role guildRole() {
        return Role.Master;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        OddJob.getInstance().log("flow");
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
        // List commands
        for (SubCommand subCommand : subCommands) {
            OddJob.getInstance().log(subCommand.toString());
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
