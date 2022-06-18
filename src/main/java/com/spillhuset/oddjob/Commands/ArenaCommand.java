package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public ArenaCommand() {
        subCommands.add(new ArenaCreateCommand());
        subCommands.add(new ArenaEditCommand());
        subCommands.add(new ArenaSetCommand());
        subCommands.add(new ArenaSaveCommand());
        /*
        subCommands.add(new ArenaListCommand());
        subCommands.add(new ArenaStartCommand());
        subCommands.add(new ArenaStopCommand());

        subCommands.add(new ArenaDestroyCommand());
        subCommands.add(new ArenaJoinCommand());
        subCommands.add(new ArenaLeaveCommand());
        */
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
        return Plugin.arena;
    }

    @Override
    public String getPermission() {
        return "arena";
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
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }

        if (sender instanceof Player player && args.length == depth()) {
            OddJob.getInstance().getGuildsManager().info(player);
            return true;
        }

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.getName().equalsIgnoreCase(args[0])) {
                    if (subCommand.can(sender, false, true)) {
                        subCommand.getCommandExecutor(sender, args);
                        return true;
                    }
                }
            }
        }
        MessageManager.sendSyntax(getPlugin(), list(sender), sender);


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
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
