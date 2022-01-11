package com.spillhuset.oddjob.Commands;
import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class WarpListCommand extends SubCommand {
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
        return Plugin.warps;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/warps list";
    }

    @Override
    public String getPermission() {
        return "warps";
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
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!can(sender,false,true)) {
            return;
        }
        if (!argsLength(sender,args.length)) {
            return;
        }
        OddJob.getInstance().getWarpManager().list(sender);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}
