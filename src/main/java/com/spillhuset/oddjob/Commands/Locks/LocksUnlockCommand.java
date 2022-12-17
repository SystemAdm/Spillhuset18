package com.spillhuset.oddjob.Commands.Locks;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Enums.Role;
import com.spillhuset.oddjob.OddJob;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import com.spillhuset.oddjob.Utils.SubCommand;
public class LocksUnlockCommand extends SubCommand {
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
        return "unlock";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/locks unlock";
    }

    @Override
    public String getPermission() {
        return "locks.unlock";
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
        OddJob.getInstance().getLocksManager().giveUnlockTool(player);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
