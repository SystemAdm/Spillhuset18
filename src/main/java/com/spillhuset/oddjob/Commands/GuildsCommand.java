package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuildsCommand extends SubCommandInterface implements CommandExecutor, TabCompleter {
    public GuildsCommand() {
        subCommands.add(new GuildsBuyCommand());
        subCommands.add(new GuildsCreateCommand());
        subCommands.add(new GuildsInfoCommand());
        subCommands.add(new GuildsListCommand());
        subCommands.add(new GuildsClaimCommand());
        subCommands.add(new GuildsUnclaimCommand());
        subCommands.add(new GuildsSetCommand());
        subCommands.add(new GuildsHomesCommand());
        subCommands.add(new GuildsInviteCommand());
        subCommands.add(new GuildsJoinCommand());
        subCommands.add(new GuildsLeaveCommand());
        subCommands.add(new GuildsAcceptCommand());
        subCommands.add(new GuildsDenyCommand());
        subCommands.add(new GuildsDisbandCommand());
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
        return Plugin.guilds;
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
        return 0;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        OddJob.getInstance().log("guilds");
        if (!can(sender, false, true)) {
            return true;
        }

        if (!argsLength(sender, args.length)) {
            return true;
        }

        // guilds buy homes
        finder(sender, args);

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        return tabs(sender,args);
    }
}
