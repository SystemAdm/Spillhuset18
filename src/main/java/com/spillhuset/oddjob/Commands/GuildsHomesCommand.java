package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.GuildInterface;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class GuildsHomesCommand extends SubCommand implements GuildInterface {
    private final List<SubCommand> subCommands = new ArrayList<>();

    public GuildsHomesCommand() {
        subCommands.add(new GuildsHomesAddCommand());
        subCommands.add(new GuildsHomesRelocateCommand());
        subCommands.add(new GuildsHomesRenameCommand());
        subCommands.add(new GuildsHomesTeleportCommand());
        subCommands.add(new GuildsHomesRemoveCommand());
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
        return "homes";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/guilds homes <name>";
    }

    @Override
    public String getPermission() {
        return "guilds";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!hasGuild(sender,true)) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length > depth() && name.equalsIgnoreCase(args[depth()])) {
                if (subCommand.can(sender,false,true)) {
                    subCommand.getCommandExecutor(sender,args);
                    return;
                }
            } else if (subCommand.can(sender,false,false)) {
                stringBuilder.append(ChatColor.GRAY).append(name).append(ChatColor.RESET).append(",");
            }
        }
        if (!stringBuilder.isEmpty()) stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        MessageManager.sendSyntax(getPlugin(), stringBuilder.toString(), sender);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();

        for (SubCommand subCommand : subCommands) {
            String name = subCommand.getName();
            if (args.length == depth() + 1) {
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
