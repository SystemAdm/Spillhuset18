package com.spillhuset.oddjob.Commands;

import com.spillhuset.oddjob.Enums.Plugin;
import com.spillhuset.oddjob.OddJob;
import com.spillhuset.oddjob.Utils.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomesBuyCommand extends SubCommand {
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
        return Plugin.homes;
    }

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/homes buy";
    }

    @Override
    public String getPermission() {
        return "homes";
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
    public void getCommandExecutor(CommandSender sender, String[] args) {
        if (!argsLength(sender, args.length)) {
            return;
        }
        if (!can(sender, false, true)) {
            return;
        }
        Player player = (Player) sender;
        OddJob.getInstance().getHomeManager().buy(player);
    }


    @Override
    public List<String> getTabCompleter(CommandSender sender, String[] args) {
        return null;
    }
}
