package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpTpCommand extends SubCommand {
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
        return "teleport";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/warp tp <name> [passwd]";
    }

    @Override
    public String getPermission() {
        return "warps";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 3;
    }

    @Override
    public int depth() {
        return 1;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        switch (args[1]) {
            case "north" -> OddJob.getInstance().getWarpManager().north((Player) sender);
            case "south" -> OddJob.getInstance().getWarpManager().south((Player) sender);
            case "east" -> OddJob.getInstance().getWarpManager().east((Player) sender);
            case "west" -> OddJob.getInstance().getWarpManager().west((Player) sender);
        }
        /*
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }

        String passwd = "";
        String name = args[1];
        if (args.length == 3) {
            passwd = args[2];
        }

        Player player = (Player) sender;
        OddJob.getInstance().getWarpManager().teleport(player, name, passwd);*/
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 2) {
            for (String name : OddJob.getInstance().getWarpManager().getList()) {
                if (args[1].isEmpty() || name.startsWith(args[1])) {
                    list.add(name);
                }
            }
        }
        return list;
    }
}
