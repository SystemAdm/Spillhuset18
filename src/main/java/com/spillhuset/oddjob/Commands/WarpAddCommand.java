package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.Managers.MessageManager;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WarpAddCommand extends SubCommand {
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
        return "add";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/warp add <name> [cost=?] [passwd=?]";
        //             0     1      2         3
    }

    @Override
    public String getPermission() {
        return "warps.admin";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public int maxArgs() {
        return 4;
    }

    @Override
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }

        String passwd = "";
        double cost = 0d;

        if (args.length > 2) {
            for (int i = 2; i < args.length; i++) {
                if (args[i].startsWith("passwd")) {
                    String pwd = args[i].split("=")[1];
                    if (!pwd.isEmpty()) passwd = pwd;
                }
                if (args[i].startsWith("cost")) {
                    String t = args[i].split("=")[1];
                    double d = Double.parseDouble(t);
                    if (d != 0d) cost = d;
                    else {
                        MessageManager.invalidNumber(getPlugin(), sender, t);
                        return;
                    }
                }
            }
        }
        Player player = (Player) sender;
        OddJob.getInstance().getWarpManager().add(player, args[1],cost,passwd);
    }

    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        List<String> options = new ArrayList<>();
        options.add("cost=");
        options.add("passwd=");
        if (args.length == 2) list.add("<name>");
        if (args.length == 3) {
            for (String opt : options) {
                if (args[2].isEmpty() || opt.startsWith(args[2])) {
                    list.add(opt);
                }
            }
        } else if (args.length == 4) {
            options.remove(args[2].substring(0,args[2].indexOf("=")+1));
            for (String opt : options) {
                if (args[3].isEmpty() || opt.startsWith(args[3])) {
                    list.add(opt);
                }
            }
        }

        return list;
    }
}
