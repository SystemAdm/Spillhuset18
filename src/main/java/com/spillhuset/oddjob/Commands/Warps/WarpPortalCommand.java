package com.spillhuset.oddjob.Commands.Warps;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WarpPortalCommand extends SubCommand {
    public WarpPortalCommand() {
        subCommands.add(new WarpPortalCreateCommand());
        subCommands.add(new WarpPortalListCommand());
        subCommands.add(new WarpPortalEditCommand());
        subCommands.add(new WarpPortalRemoveCommand());
    }
    @Override
    public boolean denyConsole() {
        return true;
    }

    @Override
    public boolean denyOp() {
        return false;
    }

    @Override
    public Plugin getPlugin() {
        return Plugin.warps;
    }

    @Override
    public String getName() {
        return "portal";
    }

    @Override
    public String getDescription() {
        return "null";
    }

    @Override
    public String getSyntax() {
        return "null";
    }

    @Override
    public String getPermission() {
        return "warps.admin";
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
        if (!can(sender,false,true)) {
            return;
        }
        if (!argsLength(sender,args.length)) {
            return;
        }
        OddJob.getInstance().log("portals");
        finder(sender,args);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        // List commands
        for (SubCommand subCommand : subCommands) {
            if (subCommand.can(sender, false, false)) {
                if (args.length == 1 || (args.length == 2 && subCommand.getName().startsWith(args[1])) && sender.hasPermission(subCommand.getPermission())) {
                    list.add(subCommand.getName());
                } else if (subCommand.getName().equalsIgnoreCase(args[1])) {
                    return subCommand.getTabCompleter(sender, args);
                }
            }
        }

        return list;
    }
}
