package com.spillhuset.oddjob.Commands.Locks;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LocksAddCommand extends SubCommand {
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
        return Plugin.locks;
    }

    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/locks add";
    }

    @Override
    public String getPermission() {
        return "locks.add";
    }

    @Override
    public int minArgs() {
        return 1;
    }

    @Override
    public int maxArgs() {
        return 1;
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
        if (!can(sender, false, true)) {
            return;
        }
        if (!argsLength(sender, args.length)) {
            return;
        }
        Player player = (Player) sender;
        OddJob.getInstance().getLocksManager().giveAddTool(player);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
